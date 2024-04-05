package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

import java.util.Set;

import static resource.ElementUtil.*;

public class SpringConfigurationBeanLoader extends AbstractBeanLoader {
    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        var bd = new BeanDefinitionModel();

        String comName = getAnnoValue(currentResource, COMPONENT);
        bd.setName(comName == null ? defaultBeanNameFromClass(currentResource) : comName);
        bd.setScope(bd.fromString(getScopeValue(currentResource)));
        bd.setType((CtClass<?>) currentResource);
        bd.setLazyInit(getLazyValue(currentResource));
        bd.setConstructor(getConstructorWithNoParams(currentResource));
        bd.setProperties(getFields(currentResource));

        String property = getAnnoValue(currentResource, CONFIG_PROPERTIES);
        bd.setFromSource(property == null ? BeanDefinitionModel.FromSource.CONFIGURATION_ANNOTATION :
                BeanDefinitionModel.FromSource.CONFIGURATION_PROPERTIES_ANNOTATION);

        bd.setPropertyValue(valueAnnoFieldValue(bd.getProperties()));
        return bd;
    }


}
