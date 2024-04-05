package analysis.processor.aop.resolver.builder;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class AroundAspectResolverBuilder<E> extends AbstractPredictResolverBuilder<E> {
    @Override
    protected String targetAnnotation() {
        return "org.aspectj.lang.annotation.Around";
    }

    @Override
    public String name() {
        return "around";
    }
}
