package analysis.processor.aop.parser;

/**
 * Created by: zhang ran
 * 2024-04-04
 */
public class ParseError extends RuntimeException {
    ParseError(String message) {
        super(message);
    }
}
