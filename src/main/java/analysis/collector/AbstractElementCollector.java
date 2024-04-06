package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
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

    protected Predicate<E> annoMatch(String annotation) {
        return (e) -> {
            var annos = e.getAnnotations();
            for (CtAnnotation<? extends Annotation> a : annos) {
                boolean isMethod = e instanceof CtMethod<?>;
                var pack = isMethod ? ((CtMethod<?>) e).getDeclaringType().getPackage() :
                        ((CtClass<?>) e).getPackage();
                if (pack == null) continue;
                if (a.getAnnotationType().getPackage().toString()
                        .equals(pack.toString())) {
                    if (a.getAnnotationType().getSimpleName()
                            .equals(annotation.substring(annotation.lastIndexOf(".") + 1))) {
                        return true;
                    }
                } else {
                    var name = a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName();
                    if (name.equals(annotation)) {
                        return true;
                    }
                }
            }
            return false;
        };
    }
}
