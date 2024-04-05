package analysis.processor.aop.parser;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by: zhang ran
 * 2024-04-04
 */
public abstract class Expr {
    public interface ExprVisitor<R> {
        R visitOrExpr(Or expr);

        R visitAndExpr(And expr);

        R visitNotExpr(Not expr);

        R visitExecutionExpr(Execution expr);

        R visitAnnotationExpr(Annotation expr);

        R visitWithinExpr(Within expr);

        R visitPointcutMethodExpr(PointcutMethod expr);


    }

    public abstract <R> R accept(ExprVisitor<R> visitor);

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

    public static class Execution extends Expr {
        Execution(Token modifier, List<Token> retType,
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
            return visitor.visitExecutionExpr(this);
        }
    }

    public static class Within extends Expr {
        Within(List<Token> declaringType) {
            this.declaringType = declaringType;
        }

        public final List<Token> declaringType;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitWithinExpr(this);
        }
    }

    public static class Annotation extends Expr {
        Annotation(List<Token> qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public final List<Token> qualifiedName;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitAnnotationExpr(this);
        }
    }

    public static class PointcutMethod extends Expr {
        PointcutMethod(List<Token> qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public final List<Token> qualifiedName;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visitPointcutMethodExpr(this);
        }
    }
}
