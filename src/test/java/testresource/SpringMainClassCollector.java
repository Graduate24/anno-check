package testresource;

import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class SpringMainClassCollector<E extends CtElement> implements ElementCollector<E> {
    private final static String SPRING_MAIN_ANNOTATION = "org.springframework.boot.autoconfigure.SpringBootApplication";

    private Predicate<E> predicate;
    private final Collection<E> result;

    public SpringMainClassCollector(Predicate<E> predicate) {
        this.predicate = predicate;
        this.result = new ArrayList<>();
    }

    public SpringMainClassCollector() {
        this.predicate = (e) -> e.getAnnotations().stream().anyMatch(a -> SPRING_MAIN_ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
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
        return this.predicate;
    }

}
