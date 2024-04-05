package analysis.processor.aop.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static analysis.processor.aop.parser.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    private boolean hasError = false;

    private static final Map<String, TokenType> keywords;


    public boolean isHasError() {
        return this.hasError;
    }

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
        try {
            while (!isAtEnd()) {
                start = current;
                scanToken();
            }
            tokens.add(new Token(EOF, ""));
            return tokens;
        } catch (ScannerError e) {
            e.printStackTrace();
            hasError = true;
        }
        return null;
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
            case '@' -> {
                if (match("annotation")) {
                    addToken(ANNOTATION);
                } else {
                    throw new ScannerError("unexpected character: " + peek());
                }
            }
            case ' ', '\r', '\t', '\n' -> {
            }
            default -> {
                if (isAlpha(c)) {
                    if (c == 'a' && match("nd ")) {
                        addToken(AND);
                    } else if (c == 'o' && match("r ")) {
                        addToken(OR);
                    } else if (c == 'n' && match("ot ")) {
                        addToken(BANG);
                    } else {
                        identifier();
                    }
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
            if (cur == '*') {
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

    private boolean match(String expected) {
        if (isAtEnd()) return false;

        char[] arr = expected.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (peek(i) != arr[i]) {
                return false;
            }
        }
        current += expected.length();
        return true;
    }

    private char peek() {
        return peek(0);
    }


    private char peekNext() {
        return peek(1);
    }

    private char peek(int dis) {
        if (current + dis >= source.length()) {
            return '\0';
        }
        return source.charAt(current + dis);
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
