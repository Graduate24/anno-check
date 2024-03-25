package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class SpringAutowiredAnnoFieldCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String SPRING_ANNOTATION = "org.springframework.beans.factory.annotation.Autowired";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> e.getAnnotations().stream().anyMatch(a -> SPRING_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.FIELD;
    }


}
