package analysis.collector;

import analysis.processor.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public abstract class AbstractElementCollector<E extends CtElement> implements ElementCollector<E> {

    private Predicate<E> predicate;
    private final Collection<E> result;

    public AbstractElementCollector(Predicate<E> predicate) {
        this.predicate = predicate;
        this.result = new ArrayList<>();
    }

    public abstract Predicate<E> defaultPredictor();

    public AbstractElementCollector() {
        this.result = new ArrayList<>();
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.CLASS;
    }

    @Override
    public Collection<E> elements() {
        return result;
    }

    @Override
    public void setMatcher(Predicate<E> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Predicate<E> getPredictor() {
        if (this.predicate == null) {
            setMatcher(defaultPredictor());
        }
        return this.predicate;
    }
}
