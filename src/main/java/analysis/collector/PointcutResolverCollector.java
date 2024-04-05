package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

public class PointcutResolverCollector<E extends CtElement> extends AbstractElementCollector<E> {
    @Override
    public Predicate<E> defaultPredictor() {
        return null;
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
