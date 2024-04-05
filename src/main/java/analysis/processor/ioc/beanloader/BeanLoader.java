package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtElement;

import java.util.Set;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public interface BeanLoader {
    BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource);
}
