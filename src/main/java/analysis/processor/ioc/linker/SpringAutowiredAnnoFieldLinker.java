package analysis.processor.ioc.linker;

import analysis.processor.ioc.beanloader.BeanDefinitionModel;
import analysis.processor.ioc.beanregistor.BeanRegister;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpringAutowiredAnnoFieldLinker implements Linker {

    private final Map<CtElement, Set<BeanDefinitionModel>> link = new HashMap<>();

    private static final String AUTOWIRED = "org.springframework.beans.factory.annotation.Autowired";
    private static final String QUALIFIER = "org.springframework.beans.factory.annotation.Qualifier";

    @Override
    public void link(CtElement element) {
        CtField<?> field = (CtField<?>) element;
//        var o = getAnnotation(element, AUTOWIRED);
//        boolean v = ((CtAnnotationImpl<?>) o).getElementValues().containsKey("required");
//        boolean required = !v || (boolean) ((CtLiteral<?>) o.getValue("required")).getValue();

        var q = getAnnotation(element, QUALIFIER);
        if (q != null) {
            String name = getValueOfAnnotationAsString(q, "value");
            var bs = BeanRegister.getBeanByName(name);
            link.put(element, bs);
            return;
        }

        var bs = BeanRegister.getBeanByName(field.getSimpleName());
        if (bs != null && !bs.isEmpty()) {
            link.put(element, bs);
            return;
        }

        bs = BeanRegister.getBeanByTypeAndSunType(field.getType().getQualifiedName());
        link.put(element, bs);
    }

    @Override
    public Set<BeanDefinitionModel> findLink(CtElement element) {
        return link.getOrDefault(element,new HashSet<>());
    }


}
