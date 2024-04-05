package analysis.processor.ioc.beanloader;

import spoon.reflect.declaration.*;

import java.util.List;
import java.util.Map;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public class BeanDefinitionModel {

    public enum BeanScope {
        SINGLETON,
        PROTOTYPE,
        REQUEST,
        SESSION,
        GLOBAL_SESSION

    }

    public enum FromSource {
        COMPONENT_ANNOTATION,
        BEAN_ANNOTATION,
        SERVICE_ANNOTATION,
        CONTROLLER_ANNOTATION,
        REPOSITORY_ANNOTATION,

        MYBATIS_MAPPER,

        MONGODB_REPOSITORY,

        CONFIGURATION_ANNOTATION,
        CONFIGURATION_PROPERTIES_ANNOTATION,
        VALUE_ANNOTATION,

    }

    public BeanScope fromString(String scope) {
        if (scope == null) {
            return BeanScope.SINGLETON;
        }
        return switch (scope) {
            case "prototype" -> BeanScope.PROTOTYPE;
            case "singleton" -> BeanScope.SINGLETON;
            case "request" -> BeanScope.REQUEST;
            case "session" -> BeanScope.SESSION;
            default -> BeanScope.GLOBAL_SESSION;
        };
    }

    static public class ConstructorArgument {
        private CtType<?> type;
        private String name;
        private String value;
    }

    private String name;
    private CtType<?> type;
    private BeanScope scope = BeanScope.SINGLETON;
    private List<ConstructorArgument> constructorArguments;
    private CtMethod<?> initializeMethod;
    private CtConstructor<?> constructor;
    private List<CtField<?>> properties;
    private Map<String, Object> propertyValue;
    private boolean lazyInit = false;

    private FromSource fromSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CtType<?> getType() {
        return type;
    }

    public void setType(CtType<?> type) {
        this.type = type;
    }

    public BeanScope getScope() {
        return scope;
    }

    public void setScope(BeanScope scope) {
        this.scope = scope;
    }

    public List<ConstructorArgument> getConstructorArguments() {
        return constructorArguments;
    }

    public void setConstructorArguments(List<ConstructorArgument> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }

    public List<CtField<?>> getProperties() {
        return properties;
    }

    public void setProperties(List<CtField<?>> properties) {
        this.properties = properties;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public CtMethod<?> getInitializeMethod() {
        return initializeMethod;
    }

    public void setInitializeMethod(CtMethod<?> initializeMethod) {
        this.initializeMethod = initializeMethod;
    }

    public CtConstructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(CtConstructor<?> constructor) {
        this.constructor = constructor;
    }

    public FromSource getFromSource() {
        return fromSource;
    }

    public void setFromSource(FromSource fromSource) {
        this.fromSource = fromSource;
    }

    public Map<String, Object> getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Map<String, Object> propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public String toString() {
        return "BeanDefinitionModel{" +
                "name=" + (name != null ? name : "") +
                ", type=" + type.getQualifiedName() +
                ", scope=" + scope + ", fromSource=" + fromSource +
                ", constructorArguments=" + constructorArguments +
                ", initializeMethod=" + (initializeMethod == null ? "" : initializeMethod.getSimpleName()) +
                ", constructor=" + (constructor == null ? "" : constructor.getSignature()) +
                ", properties=" + (properties != null ? properties.stream().map(CtNamedElement::getSimpleName).toList() : "") +
                ", propertiesValue=" + (propertyValue) +
                ", lazyInit=" + lazyInit +
                '}';
    }
}
