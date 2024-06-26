package analysis.processor.aop.parser;

/**
 * Created by: zhang ran
 * 2024-04-04
 */
public enum TokenType {
    // single-character tokens
    LEFT_PAREN, RIGHT_PAREN, COMMA, DOT, STAR, BANG,

    // two-characters
    DOT_DOT, AND, OR,

    //Literals
    IDENTIFIER, REGEX_IDENTIFIER,

    //keywords
    EXECUTION, WITHIN, ANNOTATION, PUBLIC, PROTECTED, PRIVATE, THROWS,
    EOF
}
