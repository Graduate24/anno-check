package analysis.lang.parser;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public enum TokenType {
    // single-character tokens
    LEFT_PAREN, RIGHT_PAREN, COMMA, DOT, STAR, BANG, SEMICOLON, COLON,

    // two-characters
    DOT_DOT, AND, OR, ARROW,

    //Literals
    IDENTIFIER, REGEX_IDENTIFIER, STRING,

    //keywords
    FIL_ME, FIL_INP, FIL_ANNO, FIL_MAPPER, PUBLIC, PROTECTED, PRIVATE, PUB_STATIC, THROWS, DEF, RUN,
    EOF
}
