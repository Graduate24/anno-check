package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.function.Predicate;

public class SpringRepositoryMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String SPRING_ANNOTATION = "org.springframework.stereotype.Repository";

    @Override
    public Predicate<E> defaultPredictor() {

        return (e) -> ((CtMethod<?>)e).getDeclaringType().getAnnotations().stream().anyMatch(a -> SPRING_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
