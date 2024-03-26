package analysis.processor.beanloader;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;

import java.util.Set;

/**
 * A Spring IoC container manages one or more beans. These beans are created with the configuration metadata
 * that supply to the container, for example, in the form of XML <bean/> definitions.
 * <p>
 * This class only handles configuration metadata that's annotated by implementing MongoRepository.
 */
public class SpringMongoRepoBeanLoader extends AbstractBeanLoader {

    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        var bd = new BeanDefinitionModel();
        String comName = getAnnoValue(currentResource, COMPONENT);
        bd.setName(comName == null ? defaultBeanNameFromClass(currentResource) : comName);
        bd.setScope(bd.fromString(getScopeValue(currentResource)));
        bd.setType((CtInterface<?>) currentResource);
        bd.setLazyInit(getLazyValue(currentResource));
        bd.setProperties(getFields(currentResource));
        bd.setFromSource(BeanDefinitionModel.FromSource.MONGODB_REPOSITORY);
        return bd;
    }
}
