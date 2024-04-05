package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

import java.util.Set;

import static resource.ElementUtil.*;

/**
 * A Spring IoC container manages one or more beans. These beans are created with the configuration metadata
 * that supply to the container, for example, in the form of XML <bean/> definitions.
 * <p>
 * This class only handles configuration metadata that's annotated by '@Repository'.
 * <p>
 * Created by: zhang ran
 * 2024-03-15
 */
public class SpringRepositoryAnnoBeanLoader extends AbstractBeanLoader {

    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        var bd = new BeanDefinitionModel();
        String comName = getAnnoValue(currentResource, REPOSITORY);
        bd.setName(comName == null ? defaultBeanNameFromClass(currentResource) : comName);
        bd.setScope(bd.fromString(getScopeValue(currentResource)));
        bd.setType((CtType<?>) currentResource);
        bd.setLazyInit(getLazyValue(currentResource));
        bd.setProperties(getFields(currentResource));
        bd.setFromSource(BeanDefinitionModel.FromSource.REPOSITORY_ANNOTATION);
        return bd;
    }
}
