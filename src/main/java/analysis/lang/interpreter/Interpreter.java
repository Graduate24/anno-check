package analysis.lang.interpreter;

import analysis.lang.parser.Expr;
import analysis.lang.parser.Stmt;
import analysis.lang.parser.Token;
import analysis.lang.parser.TokenType;
import io.github.azagniotov.matcher.AntPathMatcher;
import resource.CachedElementFinder;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class Interpreter implements Expr.ExprVisitor<Object>, Stmt.Visitor<Void> {

    private final Map<String, Object> locals = new HashMap<>();

    private final Set<CtMethod<?>> allMethods;
    public boolean hadRuntimeError = false;

    public Interpreter() {
        this.allMethods = CachedElementFinder.getInstance().getAllMethods();
    }

    public Interpreter(Set<CtMethod<?>> allMethods) {
        this.allMethods = allMethods;
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeException error) {
            hadRuntimeError = true;
            error.printStackTrace();
        }
    }

    @Override
    public Object visitOrExpr(Expr.Or expr) {
        Object p1 = expr.left.accept(this);
        Object p2 = expr.right.accept(this);
        if (p1 instanceof Predicate<?> && p2 instanceof Predicate) {
            return ((Predicate<?>) p1).or((Predicate) p2);
        }
        throw new RuntimeError("unsupported or operands.");
    }

    @Override
    public Object visitAndExpr(Expr.And expr) {
        Object p1 = expr.left.accept(this);
        Object p2 = expr.right.accept(this);
        if (p1 instanceof Predicate<?> && p2 instanceof Predicate) {
            return ((Predicate<?>) p1).and((Predicate) p2);
        }
        throw new RuntimeError("unsupported and operands.");
    }

    @Override
    public Object visitNotExpr(Expr.Not expr) {
        Object p1 = expr.right.accept(this);
        if (p1 instanceof Predicate<?>) {
            return ((Predicate<?>) p1).negate();
        }
        throw new RuntimeError("unsupported not operand.");
    }

    @Override
    public Object visitMethodFilterExpr(Expr.MethodFilter expr) {
        boolean ignoreModifier = expr.modifier == null;
        boolean ignoreRetType = expr.retType.size() == 1 && expr.retType.get(0).getType().equals(TokenType.STAR);
        boolean qualifiedPattern = !ignoreRetType && expr.retType.size() > 1;

        boolean ignoreDeclaringType = expr.declaringType.isEmpty() ||
                (expr.declaringType.size() == 1 && expr.declaringType.get(0).getType().equals(TokenType.STAR));
        AntPathMatcher antPathMatcher = new AntPathMatcher.Builder().build();

        boolean ignoreParam;
        ignoreParam = expr.paramPattern.size() == 1 && expr.paramPattern.get(0).getType().equals(TokenType.DOT_DOT);

        return (Predicate<CtMethod<?>>) (e) -> {
            CtMethodImpl<?> m = (CtMethodImpl<?>) e;
            if (!ignoreModifier) {
                var modifiers = m.getModifiers().stream().map(Object::toString).collect(Collectors.toSet());
                if (expr.modifier.getType().equals(TokenType.PUB_STATIC)) {
                    if (!(modifiers.contains("public") && modifiers.contains("static"))) {
                        return false;
                    }
                } else {
                    if (modifiers.stream().noneMatch(mo -> mo.equals(expr.modifier.getLexeme())))
                        return false;
                }
            }
            if (!ignoreRetType) {
                String retTypeName = qualifiedPattern ? m.getType().getQualifiedName() : m.getType().getSimpleName();
                String expected = expr.retType.stream().map(Token::getLexeme).collect(Collectors.joining("."));
                if (!retTypeName.equals(expected)) return false;
            }
            if (!ignoreDeclaringType) {
                String pattern = expr.declaringType.stream().map(Token::getLexeme).collect(Collectors.joining(""));
                String regex = pattern
                        .replaceAll("\\.\\.\\*", "/**")
                        .replaceAll("\\.", "/");
                var sig = m.getDeclaringType().getQualifiedName().replaceAll("\\.", "/");
                boolean isMatch = antPathMatcher.isMatch(regex, sig);
                if (!isMatch) return false;
            }
            String name = m.getSimpleName();
            if (expr.namePattern.getType().equals(TokenType.REGEX_IDENTIFIER)
                    || expr.namePattern.getType().equals(TokenType.STAR)) {
                if (!antPathMatcher.isMatch(expr.namePattern.getLexeme(), name)) return false;
            } else {
                if (!name.equals(expr.namePattern.getLexeme())) return false;
            }
            if (!ignoreParam) {
                var parameters = m.getParameters();
                var refinedParams = new ArrayList<String>();
                StringBuilder sb = new StringBuilder();
                for (var o : expr.paramPattern) {
                    if (o.getType().equals(TokenType.COMMA)) {
                        refinedParams.add(removeLastPeriod(sb.toString()));
                        sb = new StringBuilder();
                    }
                    sb.append(o.getLexeme());
                    sb.append(".");
                }
                if (!sb.toString().isEmpty()) {
                    refinedParams.add(removeLastPeriod(sb.toString()));
                }
                if (parameters.size() != refinedParams.size()) return false;

                for (int i = 0; i < parameters.size(); i++) {
                    if (refinedParams.get(i).contains(".")) {
                        if (!parameters.get(i).getType().getQualifiedName().equals(refinedParams.get(i))) {
                            return false;
                        }
                    } else {
                        if (!parameters.get(i).getType().getSimpleName().equals(refinedParams.get(i))) {
                            return false;
                        }
                    }
                }
            }
            //TODO ignore throws
            return true;
        };
    }

    @Override
    public Object visitAnnotationFilterExpr(Expr.AnnotationFilter expr) {
        return (Predicate<CtMethod<?>>) (e) -> {
            CtMethodImpl<?> m = (CtMethodImpl<?>) e;
            var ctPackage = e.getDeclaringType().getPackage();
            if (ctPackage == null) return false;
            var pack = ctPackage.toString();
            boolean wildcardImport = e.getAnnotations().stream()
                    .anyMatch(a -> a.getAnnotationType().getPackage().toString().equals(pack));

            var s = m.getAnnotations().stream().map(a -> wildcardImport ? a.getType().getSimpleName()
                    : a.getType().getQualifiedName()).collect(Collectors.toSet());

            if (s.isEmpty()) return false;
            var name = expr.qualifiedName.size() == 1 ? expr.qualifiedName.get(0).getLexeme() :
                    wildcardImport ? expr.qualifiedName.get(expr.qualifiedName.size() - 1).getLexeme()
                            : expr.qualifiedName.stream().map(Token::getLexeme).collect(Collectors.joining("."));
            return s.contains(name);
        };
    }

    @Override
    public Object visitPackageFilterExpr(Expr.PackageFilter expr) {
        AntPathMatcher antPathMatcher = new AntPathMatcher.Builder().build();
        String pattern = expr.declaringType.stream().map(Token::getLexeme).collect(Collectors.joining(""));
        String regex = pattern
                .replaceAll("\\.\\.\\*", "/**")
                .replaceAll("\\.", "/");
        return (Predicate<CtMethod<?>>) (e) -> {
            CtMethodImpl<?> m = (CtMethodImpl<?>) e;
            if (!m.getModifiers().contains(ModifierKind.PUBLIC)) return false;
            var sig = m.getDeclaringType().getQualifiedName().replaceAll("\\.", "/");
            return antPathMatcher.isMatch(regex, sig);
        };
    }

    @Override
    public Object visitVarExpr(Expr.Var expr) {
        Object value = locals.get(expr.name.getLexeme());
        if (value == null) {
            throw new RuntimeError("Can't find variable '" + expr.name.getLexeme() + "'.");
        }
        return value;
    }

    @Override
    public Void visitRunStmt(Stmt.Run run) {
        Token output = run.output;
        if (output == null) {
            return null;
        }
        Object exp = evaluate(run.expression);

        if (!(exp instanceof Predicate<?>)) {
            throw new RuntimeError("unsupported run expression");
        }

        Predicate<CtMethod<?>> p = (Predicate<CtMethod<?>>) exp;
        List<CtMethod<?>> result = new ArrayList<>();
        allMethods.forEach(m -> {
            if (p.test(m)) {
                result.add(m);
            }
        });

        if (output.getType().equals(TokenType.IDENTIFIER)) {
            if (output.getLexeme().equals("stdout")) {
                System.out.println("@run to stdout:---");
                result.forEach(m -> {
                    System.out.println(printMethod(m));
                });
            } else {
                locals.put(output.getLexeme(), result);
            }
        } else if (output.getType().equals(TokenType.STRING)) {
            StringBuilder sb = new StringBuilder();
            result.forEach(m -> {
                sb.append(printMethod(m));
            });
            try {
                writeOutput(output.getLexeme(), sb.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeError("unsupported output");
        }
        return null;
    }

    private void writeOutput(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Created the directory structure: " + parentDir);
            } else {
                throw new RuntimeError("Failed to create the directory structure: " + parentDir);
            }
        }
        FileWriter writer = new FileWriter(filePath, true);
        writer.write(content);
        writer.close();
    }

    @Override
    public Void visitDefStmt(Stmt.Def def) {
        Object value = evaluate(def.initializer);
        locals.put(def.name.getLexeme(), value);
        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    private String removeLastPeriod(String str) {
        if (str != null && str.endsWith(".")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private String printMethod(CtMethod<?> m) {
        StringBuilder sb = new StringBuilder();
        var modifiers = m.getModifiers();
        if (modifiers.contains(ModifierKind.PUBLIC)) {
            sb.append("public ");
        } else if (modifiers.contains(ModifierKind.PRIVATE)) {
            sb.append("private ");
        } else if (modifiers.contains(ModifierKind.PROTECTED)) {
            sb.append("protected ");
        }
        if (modifiers.contains(ModifierKind.ABSTRACT)) {
            sb.append("abstract ");
        }
        if (modifiers.contains(ModifierKind.STATIC)) {
            sb.append("static ");
        }
        if (modifiers.contains(ModifierKind.FINAL)) {
            sb.append("final ");
        }
        if (modifiers.contains(ModifierKind.NATIVE)) {
            sb.append("native ");
        }
        sb.append(m.getDeclaringType().getPackage().toString()).append(".").append(m.getSignature()).append("\n");
        return sb.toString();
    }
}
