package analysis.collector;

import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class SpringControllerClassCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ANNOTATION1 = "org.springframework.stereotype.Controller";
    private final static String ANNOTATION2 = "org.springframework.web.bind.annotation.RestController";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> {
            return e.getAnnotations().stream()
                    .anyMatch(a -> ANNOTATION1.equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName())
                            || ANNOTATION2.equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
        };
    }
}
