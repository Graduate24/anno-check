package analysis.lang.parser;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class ParseError extends RuntimeException {
    ParseError(String message) {
        super(message);
    }
}
