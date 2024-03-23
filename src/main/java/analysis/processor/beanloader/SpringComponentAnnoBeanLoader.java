package analysis.processor.beanloader;

import analysis.processor.ioccontainermodel.BeanDefinitionModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

import java.util.Set;

/**
 * A Spring IoC container manages one or more beans. These beans are created with the configuration metadata
 * that you supply to the container, for example, in the form of XML <bean/> definitions.
 * <p>
 * This class only handles configuration metadata that's annotated by '@Component'.
 */
public class SpringComponentAnnoBeanLoader extends AbstractBeanLoader {

    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        var bd = new BeanDefinitionModel();
        bd.setName(getComponentValue(currentResource));
        bd.setScope(bd.fromString(getScopeValue(currentResource)));
        bd.setType((CtClass<?>) currentResource);
        bd.setLazyInit(getLazyValue(currentResource));
        bd.setConstructor(getConstructorWithNoParams(currentResource));
        bd.setProperties(getFields(currentResource));
        return bd;
    }
}
