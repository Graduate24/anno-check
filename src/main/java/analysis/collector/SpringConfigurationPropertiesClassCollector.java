package analysis.collector;

import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class SpringConfigurationPropertiesClassCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String SPRING_ANNOTATION = "org.springframework.boot.context.properties.ConfigurationProperties";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> e.getAnnotations().stream().anyMatch(a -> SPRING_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
    }
}
