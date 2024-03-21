package analysis.collector;

import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class SpringComponentAnnoClassCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String SPRING_ANNOTATION = "org.springframework.stereotype.Component";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> e.getAnnotations().stream().anyMatch(a -> SPRING_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }
}
