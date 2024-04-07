package analysis.lang.parser;


import java.util.ArrayList;
import java.util.List;

import static analysis.lang.parser.TokenType.*;


/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class Parser {
    private final List<Token> tokens;

    private int current = 0;

    private boolean hasError = false;

    public boolean isHasError() {
        return hasError;
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> stmts = new ArrayList<>();
        while (!isAtEnd()) {
            stmts.add(declaration());
        }
        return stmts;
    }

    private Stmt declaration() {
        try {
            if (match(RUN)) {
                return statement();
            } else {
                return filterDecl();
            }
        } catch (ParseError error) {
            error.printStackTrace();
            hasError = true;
            synchronize();
            return null;
        }
    }

    private Stmt filterDecl() {
        consume(DEF, "Expect @def before filter declaration.");
        consume(IDENTIFIER, "Expect var name.");
        Token name = previous();
        consume(COLON, "Expect ':' after name");
        Expr expr = null;
        if (match(STRING)) {
            expr = new Expr.Literal(previous());
        } else {
            expr = prediction();
        }
        Stmt def = new Stmt.Def(name, expr);
        consume(SEMICOLON, "Expect ';' after def");
        return def;
    }

    private Stmt statement() {
        Expr expr;
        if (match(STRING)) {
            expr = new Expr.Literal(previous());
        } else {
            expr = prediction();
        }
        Token output = null;
        Token operator = null;
        if (match(ARROW)) {
            operator = previous();
            if (match(IDENTIFIER)) {
                output = previous();
            } else if (match(STRING)) {
                output = previous();
            } else {
                throw new ParseError("Expect output");
            }
        }
        Stmt run = new Stmt.Run(expr, operator, output);
        consume(SEMICOLON, "Expect ';' after def");
        return run;
    }

    private Expr prediction() {
        Expr expr = term();
        while (match(OR)) {
            Expr right = term();
            expr = new Expr.Or(expr, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = unary();
        while (match(AND)) {
            Expr right = unary();
            expr = new Expr.And(expr, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(BANG)) {
            return new Expr.Not(unary());
        }
        return factor();
    }

    private Expr factor() {
        if (match(IDENTIFIER)) {
            Token name = previous();
            return new Expr.Var(name);
        } else if (match(LEFT_PAREN)) {
            Expr expr = prediction();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return expr;
        } else if (match(STRING)) {
            Token string = previous();
            return new Expr.Literal(string);
        } else {
            return filter();
        }
    }

    private Expr filter() {
        if (match(FIL_ME)) {
            consume(LEFT_PAREN, "Expect '(' before filme.");
            Expr expr = filme();
            consume(RIGHT_PAREN, "Expect ')' after filme.");
            return expr;
        } else if (match(FIL_INP)) {
            consume(LEFT_PAREN, "Expect '(' before filme.");
            Expr expr = filinp();
            consume(RIGHT_PAREN, "Expect ')' after filme.");
            return expr;
        } else if (match(FIL_ANNO)) {
            consume(LEFT_PAREN, "Expect '(' before filanno.");
            Expr expr = filanno();
            consume(RIGHT_PAREN, "Expect ')' after filanno.");
            return expr;
        } else if (match(FIL_MAPPER)) {
            consume(LEFT_PAREN, "Expect '(' before filminmapper.");
            Expr expr = new Expr.MapperFilter();
            consume(RIGHT_PAREN, "Expect ')' after filminmapper.");
            return expr;
        } else if (match(FIL_CANNO)) {
            consume(LEFT_PAREN, "Expect '(' before filcanno.");
            Expr expr = filcanno();
            consume(RIGHT_PAREN, "Expect ')' after filcanno.");
            return expr;
        } else {
            throw new ParseError("filter not found");
        }
    }

    private List<Token> qualifiedName(boolean keepDots, TokenType... dotTypes) {
        List<Token> names = new ArrayList<>();
        consume(IDENTIFIER, "Expect identifier in qualifiedName.");
        names.add(previous());
        while (match(dotTypes)) {
            if (keepDots) {
                names.add(previous());
            }
            consume(IDENTIFIER, "Expect identifier in qualifiedName.");
            names.add(previous());
        }
        return names;
    }

    private Expr filanno() {
        return new Expr.AnnotationFilter(qualifiedName(false, DOT));
    }

    private Expr filcanno() {
        return new Expr.ClassAnnotationFilter(qualifiedName(false, DOT));
    }

    private Expr filinp() {
        List<Token> declaringType = new ArrayList<>();
        consume(IDENTIFIER, "within type must starts with identifier");
        declaringType.add(previous());
        while (match(DOT, DOT_DOT)) {
            // add dots
            declaringType.add(previous());
            if (match(IDENTIFIER, STAR)) {
                declaringType.add(previous());
            } else {
                throw new ParseError("identifier or star must follow dot");
            }
        }
        return new Expr.PackageFilter(declaringType);
    }

    private Expr filme() {
        Token modifier = null;
        if (match(PUBLIC, PROTECTED, PRIVATE, PUB_STATIC)) {
            modifier = previous();
        }
        List<Token> retType = new ArrayList<>();
        if (match(STAR)) {
            retType.add(previous());
        } else {
            retType = qualifiedName(false, DOT);
        }
        List<Token> declaringTypeAndName = new ArrayList<>();
        Token namePattern = null;
        if (match(STAR, REGEX_IDENTIFIER)) {
            // must be name pattern and no declaring type pattern
            namePattern = previous();
        } else {
            consume(IDENTIFIER, "declaring type must starts with identifier");
            declaringTypeAndName.add(previous());
            while (match(DOT, DOT_DOT)) {
                // add dots
                declaringTypeAndName.add(previous());
                if (match(IDENTIFIER, REGEX_IDENTIFIER, STAR)) {
                    declaringTypeAndName.add(previous());
                } else if (peek().type == LEFT_PAREN) {
                    break;
                } else {
                    throw new ParseError("identifier or star must follow dot");
                }
            }
        }

        if (namePattern == null) {
            if (peek().type == LEFT_PAREN) {
                advance();
                namePattern = declaringTypeAndName.get(declaringTypeAndName.size() - 1);
                declaringTypeAndName.remove(declaringTypeAndName.size() - 1);
                if (!declaringTypeAndName.isEmpty() && declaringTypeAndName.get(declaringTypeAndName.size() - 1).type == DOT) {
                    declaringTypeAndName.remove(declaringTypeAndName.size() - 1);
                }
            } else {
                throw new ParseError("missing '(' after name pattern");
            }
        } else {
            consume(LEFT_PAREN, "missing '(' after name pattern");
        }

        List<Token> paramPattern = new ArrayList<>();

        if (match(DOT_DOT)) {
            paramPattern.add(previous());
            consume(RIGHT_PAREN, "expect ')' after param pattern");
        } else if (match(RIGHT_PAREN)) {
            // end
        } else {
            paramPattern.addAll(qualifiedName(false, DOT));
            while (match(COMMA)) {
                // add comma
                paramPattern.add(previous());
                paramPattern.addAll(qualifiedName(false, DOT));
            }
            consume(RIGHT_PAREN, "expect ')' after param pattern");
        }

        List<Token> throwsList = new ArrayList<>();
        if (match(THROWS)) {
            throwsList.addAll(qualifiedName(false, DOT));
            while (match(COMMA)) {
                // add comma
                throwsList.add(previous());
                throwsList.addAll(qualifiedName(false, DOT));
            }
        }
        return new Expr.MethodFilter(modifier, retType, declaringTypeAndName, namePattern, paramPattern, throwsList);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw new ParseError(message + ". Actual type: " + peek().type);
    }

    private boolean checkNext(TokenType tokenType) {
        if (isAtEnd()) return false;
        if (tokens.get(current + 1).type == EOF) return false;
        return tokens.get(current + 1).type == tokenType;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private boolean match(TokenType... tokenTypes) {
        for (var type : tokenTypes) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) {
                return;
            }
            advance();
        }
    }
}
