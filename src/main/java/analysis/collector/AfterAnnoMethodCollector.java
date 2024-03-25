package analysis.collector;

import analysis.processor.resourcescanner.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class AfterAnnoMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ANNOTATION = "org.aspectj.lang.annotation.After";

    @Override
    public Predicate<E> defaultPredictor() {

        return (e) -> e.getAnnotations().stream().anyMatch(a -> ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
