package analysis.processor.aop.parser;

public class ParseError extends RuntimeException {
    ParseError(String message) {
        super(message);
    }
}
