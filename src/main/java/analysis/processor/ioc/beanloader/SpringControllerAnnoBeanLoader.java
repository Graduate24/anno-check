package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

import java.util.Set;

import static resource.ElementUtil.*;

/**
 * A Spring IoC container manages one or more beans. These beans are created with the configuration metadata
 * that supply to the container, for example, in the form of XML <bean/> definitions.
 * <p>
 * This class only handles configuration metadata that's annotated by '@Controller'.
 * <p>
 * Created by: zhang ran
 * 2024-03-15
 */
public class SpringControllerAnnoBeanLoader extends AbstractBeanLoader {

    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        var bd = new BeanDefinitionModel();
        String comName = getAnnoValue(currentResource, CONTROLLER);
        bd.setName(comName == null ? defaultBeanNameFromClass(currentResource) : comName);
        bd.setScope(bd.fromString(getScopeValue(currentResource)));
        bd.setType(((CtClass<?>) currentResource).getQualifiedName());
        bd.setLazyInit(getLazyValue(currentResource));
        bd.setConstructor(getConstructorWithNoParams(currentResource));
        bd.setProperties(getFields(currentResource));
        bd.setFromSource(BeanDefinitionModel.FromSource.CONTROLLER_ANNOTATION);
        return bd;
    }
}
