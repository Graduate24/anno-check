package analysis.collector;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class AspectAnnoClassCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ASPECT_ANNOTATION = "org.aspectj.lang.annotation.Aspect";

    @Override
    public Predicate<E> defaultPredictor() {
        return annoMatch(ASPECT_ANNOTATION);
    }
}
