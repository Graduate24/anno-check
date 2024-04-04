package analysis.processor.aop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static analysis.processor.aop.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    private static final Map<String, TokenType> keywords;


    static {
        keywords = new HashMap<>();
        keywords.put("execution", EXECUTION);
        keywords.put("within", WITHIN);
        keywords.put("@annotation", ANNOTATION);
        keywords.put("public", PUBLIC);
        keywords.put("protected", PROTECTED);
        keywords.put("private", PRIVATE);
        keywords.put("throws", THROWS);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, ""));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case ',' -> addToken(COMMA);
            case '.' -> {
                if (match('.')) {
                    addToken(DOT_DOT);
                } else {
                    addToken(DOT);
                }
            }
            case '*' -> {
                char prev = c;
                boolean regexIdentifier = false;
                while (isAlphaNumeric(peek()) || peek() == '*') {
                    char cur = advance();
                    if (cur == '*' && prev == '*') {
                        throw new ScannerError("can't have consecutive '*'s ");
                    }
                    regexIdentifier = true;
                    prev = cur;
                }
                if (regexIdentifier) {
                    addToken(REGEX_IDENTIFIER);
                } else {
                    addToken(STAR);
                }
            }
            case '!' -> addToken(BANG);
            case '|' -> {
                if (match('|')) {
                    addToken(OR);
                } else {
                    throw new ScannerError("unexpected character: " + peek());
                }
            }
            case '&' -> {
                if (match('&')) {
                    addToken(AND);
                } else {
                    throw new ScannerError("unexpected character: " + peek());
                }
            }
            case ' ', '\r', '\t', '\n' -> {
            }
            default -> {
                if (isAlpha(c)) {
                    identifier();
                } else {
                    throw new ScannerError("unexpected character: " + c);
                }

            }
        }
    }

    private void identifier() {
        char prev = '\0';
        boolean regexIdentifier = false;
        while (isAlphaNumeric(peek()) || peek() == '*') {
            char cur = advance();
            if (cur == '*' && prev == '*') {
                throw new ScannerError("can't have consecutive '*'s ");
            }
            if(cur=='*'){
                regexIdentifier = true;
            }
            prev = cur;
        }
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            if (regexIdentifier) {
                type = REGEX_IDENTIFIER;
            } else {
                type = IDENTIFIER;
            }
        }
        addToken(type);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }


    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }


    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
