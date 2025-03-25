package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.Set;

import static resource.ElementUtil.*;

/**
 * Spring components can also contribute bean definition metadata to the container.
 * To do this with the same @Bean annotation used to define bean metadata within @Configuration annotated classes.
 * <p>
 * This class only handles configuration metadata that's annotated by '@Bean'.
 * <p>
 * Created by: zhang ran
 * 2024-03-15
 */
public class SpringBeanAnnoBeanLoader extends AbstractBeanLoader {

    @Override
    public BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource) {
        CtMethod<?> currentMethod = (CtMethod<?>) currentResource;

        var bd = new BeanDefinitionModel();
        String comName = getAnnoValue(currentMethod, BEAN);
        if (comName == null) {
            comName = getAnnoValue(currentMethod, QUALIFIER);
            if (comName == null) {
                comName = currentMethod.getSimpleName();
            }
        }
        bd.setName(comName);
        bd.setScope(bd.fromString(getScopeValue(currentMethod)));
        bd.setType(currentMethod.getDeclaringType().getQualifiedName());
        bd.setLazyInit(getLazyValue(currentMethod));
        bd.setInitializeMethod(currentMethod);
        bd.setFromSource(BeanDefinitionModel.FromSource.BEAN_ANNOTATION);
        return bd;
    }
}
