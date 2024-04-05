package analysis.processor.aop.parser;

/**
 * Created by: zhang ran
 * 2024-04-04
 */
public class ScannerError extends RuntimeException {
    ScannerError(String message) {
        super(message);
    }
}
