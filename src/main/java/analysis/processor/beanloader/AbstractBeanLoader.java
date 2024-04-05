package analysis.processor.beanloader;

import resource.ModelFactory;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.*;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


}
