package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class BeforeAnnoMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ANNOTATION = "org.aspectj.lang.annotation.Before";

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
