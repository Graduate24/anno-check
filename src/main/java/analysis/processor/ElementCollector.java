package analysis.processor;

import java.util.Collection;
import java.util.function.Predicate;

public interface ElementCollector<E> {
    Collection<E> elements();
    void setMatcher(Predicate<E> predicate);
}
