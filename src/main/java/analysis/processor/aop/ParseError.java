package analysis.processor.aop;

public class ParseError extends RuntimeException {
    ParseError(String message) {
        super(message);
    }
}
