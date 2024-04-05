package analysis.processor.ioc.beanloader;

import resource.ModelFactory;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static resource.ElementUtil.get$placeholder;
import static resource.ElementUtil.getAnnoValue;

/**
 * Spring components can also contribute bean definition metadata to the container.
 * To do this with the same @Bean annotation used to define bean metadata within @Configuration annotated classes.
 * <p>
 * This class only handles configuration metadata that's annotated by '@Value'.
 * <p>
 * Created by: zhang ran
 * 2024-03-15
 */
public class SpringValueAnnoBeanLoader extends AbstractBeanLoader {

    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        CtField<?> field = (CtField<?>) currentResource;
        var bd = new BeanDefinitionModel();
        String name = field.getDeclaringType().getQualifiedName() + "#" + field.getSimpleName();
        bd.setName(name);

        // ${ }
        String value = getAnnoValue(field, VALUE_ANNO);
        if (value == null) return null;
        value = get$placeholder(value);
        if (value == null) return null;
        var v = ModelFactory.getConfigFromName(value);
        if (v == null) return null;
        bd.setType(field.getDeclaringType());
        bd.setFromSource(BeanDefinitionModel.FromSource.VALUE_ANNOTATION);
        Map<String, Object> pv = new HashMap<>();
        pv.put(field.getSimpleName(), v);
        bd.setPropertyValue(pv);
        return bd;
    }
}
