package analysis.collector;

import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class SpringMainClassCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String SPRING_ANNOTATION = "org.springframework.boot.autoconfigure.SpringBootApplication";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> e.getAnnotations().stream().anyMatch(a -> SPRING_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }
}
