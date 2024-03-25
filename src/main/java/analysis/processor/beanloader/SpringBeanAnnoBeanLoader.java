package analysis.processor.beanloader;

import analysis.processor.ioccontainermodel.BeanDefinitionModel;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.Set;

/**
 * Spring components can also contribute bean definition metadata to the container.
 * To do this with the same @Bean annotation used to define bean metadata within @Configuration annotated classes.
 * <p>
 * This class only handles configuration metadata that's annotated by '@Bean'.
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
        bd.setType((CtClass<?>) currentMethod.getDeclaringType());
        bd.setLazyInit(getLazyValue(currentMethod));
        bd.setInitializeMethod(currentMethod);
        bd.setFromSource(BeanDefinitionModel.FromSource.BEAN_ANNOTATION);
        return bd;
    }
}