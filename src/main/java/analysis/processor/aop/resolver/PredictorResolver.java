package analysis.processor.aop.resolver;

import analysis.processor.aop.parser.Expr;
import analysis.processor.aop.parser.Token;
import analysis.processor.aop.parser.TokenType;
import analysis.processor.aop.resolver.builder.CachedPredictPointcutResolverBuilder;
import io.github.azagniotov.matcher.AntPathMatcher;
import resource.CachedElementFinder;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by: zhang ran
 * 2024-04-05
 */
public class PredictorResolver<T> implements Expr.ExprVisitor<Predicate<T>> {
    private final CachedElementFinder finder = CachedElementFinder.getInstance();

    public Expr expr;
    public String base;

    public String declaringClass;

    public PredictorResolver(Expr expr, String basePackage, String declaringClass) {
        this.expr = expr;
        this.base = basePackage;
        this.declaringClass = declaringClass;
    }

    public Predicate<T> resolvePredictor() {
        return this.expr.accept(this);
    }

    @Override
    public Predicate<T> visitOrExpr(Expr.Or expr) {
        Predicate<T> p1 = expr.left.accept(this);
        Predicate<T> p2 = expr.right.accept(this);
        return p1.or(p2);
    }

    @Override
    public Predicate<T> visitAndExpr(Expr.And expr) {
        Predicate<T> p1 = expr.left.accept(this);
        Predicate<T> p2 = expr.right.accept(this);
        return p1.and(p2);
    }

    @Override
    public Predicate<T> visitNotExpr(Expr.Not expr) {
        Predicate<T> p1 = expr.right.accept(this);
        return p1.negate();
    }

    /**
     * Due to the proxy-based nature of Spring’s AOP framework, protected methods are by definition not intercepted,
     * neither for JDK proxies (where this isn’t applicable) nor for CGLIB proxies (where this is technically
     * possible but not recommendable for AOP purposes).
     */
    @Override
    public Predicate<T> visitExecutionExpr(Expr.Execution expr) {

        // ignore modifier, only test public methods
        boolean ignoreRetType;
        boolean qualifiedPattern;
        if (expr.retType.size() == 1 && expr.retType.get(0).getType().equals(TokenType.STAR)) {
            qualifiedPattern = false;
            ignoreRetType = true;
        } else {
            ignoreRetType = false;
            qualifiedPattern = expr.retType.size() > 1;
        }
        boolean ignoreDeclaringType;
        ignoreDeclaringType = expr.declaringType.isEmpty() ||
                (expr.declaringType.size() == 1 && expr.declaringType.get(0).getType().equals(TokenType.STAR));
        AntPathMatcher antPathMatcher = new AntPathMatcher.Builder().build();

        boolean ignoreParam;
        ignoreParam = expr.paramPattern.size() == 1 && expr.paramPattern.get(0).getType().equals(TokenType.DOT_DOT);

        return (e) -> {
            CtMethodImpl<?> m = (CtMethodImpl<?>) e;
            if (!m.getModifiers().contains(ModifierKind.PUBLIC)) return false;
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

                boolean isMatch = antPathMatcher.isMatch(regex, m.getSignature().substring(0, m.getSignature().lastIndexOf(".")));
                if (!isMatch) return false;
            }
            String name = m.getSimpleName();
            if (expr.namePattern.getType().equals(TokenType.REGEX_IDENTIFIER)) {
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
                refinedParams.add(removeLastPeriod(sb.toString()));

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
    public Predicate<T> visitAnnotationExpr(Expr.Annotation expr) {

        return (e) -> {
            CtMethodImpl<?> m = (CtMethodImpl<?>) e;
            if (!m.getModifiers().contains(ModifierKind.PUBLIC)) return false;
            return m.getAnnotations().stream().map(a -> a.getType().getQualifiedName())
                    .collect(Collectors.toSet())
                    .contains(expr.qualifiedName.size() == 1 ? base + "." + expr.qualifiedName.get(0) :
                            expr.qualifiedName.stream()
                                    .map(Token::getLexeme).collect(Collectors.joining(".")));
        };
    }

    @Override
    public Predicate<T> visitWithinExpr(Expr.Within expr) {
        AntPathMatcher antPathMatcher = new AntPathMatcher.Builder().build();
        String pattern = expr.declaringType.stream().map(Token::getLexeme).collect(Collectors.joining(""));
        String regex = pattern
                .replaceAll("\\.\\.\\*", "/**")
                .replaceAll("\\.", "/");


        return (e) -> {
            CtMethodImpl<?> m = (CtMethodImpl<?>) e;
            if (!m.getModifiers().contains(ModifierKind.PUBLIC)) return false;
            return antPathMatcher.isMatch(regex, m.getSignature().substring(0, m.getSignature().lastIndexOf(".")));
        };
    }

    @Override
    public Predicate<T> visitPointcutMethodExpr(Expr.PointcutMethod expr) {
        String methodName;
        if (expr.qualifiedName.size() == 1) {
            methodName = base + "." + declaringClass + "." + expr.qualifiedName.get(0);
        } else {
            methodName = expr.qualifiedName.stream().map(Token::getLexeme).collect(Collectors.joining("."));
        }
        var method = finder.findPointcutMethod(methodName);
        if (method == null) {
            throw new ResolverError("Can't find method:[" + methodName + "]");
        }

        var builder = new CachedPredictPointcutResolverBuilder<T>();
        return builder.build(null, method);
    }

    private String removeLastPeriod(String str) {
        if (str != null && str.endsWith(".")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
