package analysis.processor.aop;

public class ScannerError extends RuntimeException {
    ScannerError(String message) {
        super(message);
    }
}
