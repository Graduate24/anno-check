package analysis.collector;

import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class AspectAnnoClassCollector <E extends CtElement> extends AbstractElementCollector<E>{
    private final static String ASPECT_ANNOTATION = "org.aspectj.lang.annotation.Aspect";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> e.getAnnotations().stream().anyMatch(a -> ASPECT_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }
}
