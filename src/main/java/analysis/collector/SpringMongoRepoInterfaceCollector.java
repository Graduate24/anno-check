package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;

import java.util.function.Predicate;

public class SpringMongoRepoInterfaceCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String SUPER = "org.springframework.data.mongodb.repository.MongoRepository";

    @Override
    public Predicate<E> defaultPredictor() {
        return (e) -> ((CtInterface<?>)e).getSuperInterfaces().stream().anyMatch(s -> s.getQualifiedName().equals(SUPER));
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.INTERFACE;
    }
}
