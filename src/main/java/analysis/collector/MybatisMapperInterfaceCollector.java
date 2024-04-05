package analysis.collector;

import resource.ModelFactory;
import resource.ResourceRole;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;

import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class MybatisMapperInterfaceCollector<E extends CtElement> extends AbstractElementCollector<E> {

    private static final String ANNOTATION = "org.apache.ibatis.annotations.Mapper";

    @Override
    public Predicate<E> defaultPredictor() {
        return null;
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.INTERFACE;
    }

    @Override
    public void collect(Object e) {

        boolean isMapper = ModelFactory.getMybatisMapper().contains(((CtInterface<?>) e).getQualifiedName());
        if (isMapper) {
            elements().add((E) e);
            return;
        }

        boolean isAnnoMapper = ((CtInterface<?>) e).getAnnotations().stream().anyMatch(a -> ANNOTATION.
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));
        if (isAnnoMapper) {
            elements().add((E) e);
        }
    }

}
