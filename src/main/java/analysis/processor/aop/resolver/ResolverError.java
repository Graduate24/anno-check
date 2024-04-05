package analysis.processor.aop.resolver;

/**
 * Created by: zhang ran
 * 2024-04-05
 */
public class ResolverError extends RuntimeException {
    ResolverError(String message) {
        super(message);
    }
}
