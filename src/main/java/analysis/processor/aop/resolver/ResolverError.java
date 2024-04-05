package analysis.processor.aop.resolver;

public class ResolverError extends RuntimeException{
    ResolverError(String message) {
        super(message);
    }
}
