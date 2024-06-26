package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class AfterReturningAnnoMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ANNOTATION = "org.aspectj.lang.annotation.AfterReturning";

    @Override
    public Predicate<E> defaultPredictor() {
        return annoMatch(ANNOTATION);
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
