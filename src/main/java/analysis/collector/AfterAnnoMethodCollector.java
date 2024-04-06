package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class AfterAnnoMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ANNOTATION = "org.aspectj.lang.annotation.After";

    @Override
    public Predicate<E> defaultPredictor() {
        return annoMatch(ANNOTATION);
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
