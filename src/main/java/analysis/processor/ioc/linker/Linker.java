package analysis.processor.ioc.linker;

import analysis.processor.ioc.beanloader.BeanDefinitionModel;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.Set;

/**
 * Created by: zhang ran
 * 2024-03-18
 */
public interface Linker {
    void link(CtElement element);

    Set<BeanDefinitionModel> findLink(CtElement element);

    default CtAnnotation<?> getAnnotation(CtElement element, String annotationName) {
        var o = element.getAnnotations().stream().filter(v -> v.getAnnotationType().getQualifiedName()
                .equals(annotationName)).findFirst();
        return o.orElse(null);
    }

    default String getValueOfAnnotationAsString(CtAnnotation<?> o, String key) {
        boolean v = ((CtAnnotationImpl<?>) o).getElementValues().containsKey(key);
        return v ? (String) ((CtLiteral<?>) o.getValue(key)).getValue() : null;
    }

}
