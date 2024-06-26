package analysis.processor.aop.resolver.builder;

/**
 * Created by: zhang ran
 * 2024-04-05
 */
public class CachedPredictPointcutResolverBuilder<E> extends AbstractPredictResolverBuilder<E> {

    @Override
    protected String targetAnnotation() {
        return "org.aspectj.lang.annotation.Pointcut";
    }

    @Override
    public String name() {
        return "pointcut";
    }
}
