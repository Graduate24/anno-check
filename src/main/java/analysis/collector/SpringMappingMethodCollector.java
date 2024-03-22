package analysis.collector;

import analysis.processor.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class SpringMappingMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String patternString = "org\\.springframework\\.web\\.bind\\.annotation\\..*Mapping";

    Pattern pattern = Pattern.compile(patternString);

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> e.getAnnotations().stream().anyMatch(a ->
                pattern.matcher(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()).matches());
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
