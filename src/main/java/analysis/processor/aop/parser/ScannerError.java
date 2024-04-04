package analysis.processor.aop.parser;

public class ScannerError extends RuntimeException {
    ScannerError(String message) {
        super(message);
    }
}
