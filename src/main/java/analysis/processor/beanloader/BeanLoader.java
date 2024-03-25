package analysis.processor.beanloader;

import spoon.reflect.declaration.CtElement;

import java.util.Set;

public interface BeanLoader {
    BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource);
}
