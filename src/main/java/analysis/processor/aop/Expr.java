package analysis.processor.aop;

import com.google.gson.Gson;

import java.util.List;

public class Expr {

    static class Or extends Expr {
        Or(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        final Expr left;
        final Expr right;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    static class And extends Expr {
        And(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        final Expr left;
        final Expr right;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    static class Not extends Expr {
        Not(Expr right) {
            this.right = right;
        }

        final Expr right;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    static class Identifier extends Expr {

    }

    static class Execution extends Expr {
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

        final Token modifier;
        final List<Token> retType;
        final List<Token> declaringType;
        final Token namePattern;
        final List<Token> paramPattern;
        final List<Token> throwsPattern;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

    }

    static class PointcutMethod extends Expr {
        PointcutMethod(List<Token> qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        final List<Token> qualifiedName;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
