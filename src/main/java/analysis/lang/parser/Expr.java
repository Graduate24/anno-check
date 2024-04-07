package analysis.lang.parser;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public abstract class Expr {
    public interface ExprVisitor<R> {
        R visitOrExpr(Or expr);

        R visitAndExpr(And expr);

        R visitNotExpr(Not expr);

        R visitMethodFilterExpr(MethodFilter expr);

        R visitAnnotationFilterExpr(AnnotationFilter expr);

        R visitPackageFilterExpr(PackageFilter expr);

        R visitVarExpr(Var expr);

        R visitMapperExpr(MapperFilter expr);

        R visitClassAnnotationExpr(ClassAnnotationFilter expr);

        R visitLiteralExpr(Literal expr);
    }

    public abstract <R> R accept(Expr.ExprVisitor<R> visitor);

    public static class Or extends Expr {
        Or(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        public final Expr left;
        public final Expr right;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitOrExpr(this);
        }
    }

    public static class And extends Expr {
        And(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        public final Expr left;
        public final Expr right;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitAndExpr(this);
        }
    }

    public static class Not extends Expr {
        Not(Expr right) {
            this.right = right;
        }

        public final Expr right;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitNotExpr(this);
        }
    }

    public static class MethodFilter extends Expr {
        MethodFilter(Token modifier, List<Token> retType,
                     List<Token> declaringType, Token namePattern,
                     List<Token> paramPattern, List<Token> throwsPattern) {
            this.modifier = modifier;
            this.retType = retType;
            this.declaringType = declaringType;
            this.namePattern = namePattern;
            this.paramPattern = paramPattern;
            this.throwsPattern = throwsPattern;
        }

        public final Token modifier;
        public final List<Token> retType;
        public final List<Token> declaringType;
        public final Token namePattern;
        public final List<Token> paramPattern;
        public final List<Token> throwsPattern;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitMethodFilterExpr(this);
        }
    }

    public static class PackageFilter extends Expr {
        PackageFilter(List<Token> declaringType) {
            this.declaringType = declaringType;
        }

        public final List<Token> declaringType;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitPackageFilterExpr(this);
        }
    }

    public static class AnnotationFilter extends Expr {
        AnnotationFilter(List<Token> qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public final List<Token> qualifiedName;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitAnnotationFilterExpr(this);
        }
    }

    public static class ClassAnnotationFilter extends Expr {
        ClassAnnotationFilter(List<Token> qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public final List<Token> qualifiedName;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitClassAnnotationExpr(this);
        }
    }

    public static class MapperFilter extends Expr {
        MapperFilter() {
        }

        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitMapperExpr(this);
        }
    }

    public static class Var extends Expr {
        Var(Token name) {
            this.name = name;
        }

        public final Token name;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitVarExpr(this);
        }
    }

    public static class Literal extends Expr {
        Literal(Token literal) {
            this.literal = literal;
        }

        public final Token literal;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

}
