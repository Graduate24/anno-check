package analysis.lang.parser;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class ScannerError extends RuntimeException {
    ScannerError(String message) {
        super(message);
    }
}
