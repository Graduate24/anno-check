package analysis.lang.parser;

import com.google.gson.Gson;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public abstract class Stmt {

    public interface Visitor<R> {
        R visitRunStmt(Stmt.Run run);

        R visitDefStmt(Stmt.Def def);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Run extends Stmt {
        Run(Expr expression, Token output) {
            this.expression = expression;
            this.output = output;
        }

        public final Expr expression;
        public final Token output;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRunStmt(this);
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class Def extends Stmt {
        Def(Token name, Expr initializer) {
            this.initializer = initializer;
            this.name = name;
        }

        public final Expr initializer;
        public final Token name;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitDefStmt(this);
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
