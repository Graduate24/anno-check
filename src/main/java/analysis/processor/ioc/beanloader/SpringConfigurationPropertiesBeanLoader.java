package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

import java.util.Set;

import static resource.ElementUtil.*;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public class SpringConfigurationPropertiesBeanLoader extends AbstractBeanLoader {
    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        var bd = new BeanDefinitionModel();

        String comName = getAnnoValue(currentResource, COMPONENT);
        bd.setName(comName == null ? defaultBeanNameFromClass(currentResource) : comName);
        bd.setScope(bd.fromString(getScopeValue(currentResource)));
        bd.setType(((CtClass<?>) currentResource).getQualifiedName());
        bd.setLazyInit(getLazyValue(currentResource));
        bd.setConstructor(getConstructorWithNoParams(currentResource));
        bd.setProperties(getFields(currentResource));

        String property = getAnnoValue(currentResource, CONFIG_PROPERTIES);
        if (property == null) {
            property = getAnnoValue(currentResource, CONFIG_PROPERTIES, "prefix");
        }
        bd.setFromSource(BeanDefinitionModel.FromSource.CONFIGURATION_PROPERTIES_ANNOTATION);
        bd.setPropertyValue(valueAnnoFieldValue(bd.getProperties(), property));
        return bd;
    }
}
