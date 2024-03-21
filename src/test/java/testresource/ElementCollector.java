package testresource;

import spoon.reflect.declaration.CtElement;

import java.util.Collection;
import java.util.function.Predicate;

public interface ElementCollector<E extends CtElement> {

    ResourceRole role();

    Collection<E> elements();

    void setMatcher(Predicate<E> predicate);

    Predicate<E> getPredictor();

    default void collect(Object e) {
        if (getPredictor().test((E) e)) {
            elements().add((E) e);
        }
    }
}
