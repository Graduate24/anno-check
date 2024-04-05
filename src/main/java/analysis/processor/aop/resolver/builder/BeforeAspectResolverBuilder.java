package analysis.processor.aop.resolver.builder;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class BeforeAspectResolverBuilder<E> extends AbstractPredictResolverBuilder<E> {

    @Override
    protected String targetAnnotation() {
        return "org.aspectj.lang.annotation.Before";
    }

    @Override
    public String name() {
        return "before";
    }
}
