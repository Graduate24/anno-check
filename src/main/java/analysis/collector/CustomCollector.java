package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;

import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class CustomCollector {

    public static <E extends CtElement> ElementCollector<E> newCollector(Predicate<E> predicate, ResourceRole role) {
        return new AbstractElementCollector<>() {
            @Override
            public Predicate<E> defaultPredictor() {
                return predicate;
            }

            @Override
            public ResourceRole role() {
                return role;
            }
        };
    }
}
