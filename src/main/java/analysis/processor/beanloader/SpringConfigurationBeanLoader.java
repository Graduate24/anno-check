package analysis.processor.beanloader;

import resource.ModelFactory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
