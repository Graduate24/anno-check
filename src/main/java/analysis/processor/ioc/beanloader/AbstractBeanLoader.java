package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.CtElement;

import java.util.Set;

public abstract class AbstractBeanLoader implements BeanLoader {
    protected final static String COMPONENT = "org.springframework.stereotype.Component";
    protected final static String SERVICE = "org.springframework.stereotype.Service";
    protected final static String CONTROLLER = "org.springframework.stereotype.Controller";
    protected final static String SCOPE = "org.springframework.context.annotation.Scope";
    protected final static String LAZY = "org.springframework.context.annotation.Lazy";
    protected final static String QUALIFIER = "org.springframework.beans.factory.annotation.Qualifier";
    protected final static String BEAN = "org.springframework.context.annotation.Bean";
    protected final static String REPOSITORY = "org.springframework.stereotype.Repository";
    protected final static String CONFIG_PROPERTIES = "org.springframework.boot.context.properties.ConfigurationProperties";
    protected final static String MAPPER = "org.apache.ibatis.annotations.Mapper";
    private final static String VALUE = "value";

    protected final static String VALUE_ANNO = "org.springframework.beans.factory.annotation.Value";


    public abstract BeanDefinitionModel load(Set<CtElement> contextResource, CtElement currentResource);
}
