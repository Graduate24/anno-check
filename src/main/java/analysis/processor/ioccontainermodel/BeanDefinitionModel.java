package analysis.processor.ioccontainermodel;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.List;

public class BeanDefinitionModel {

    public enum BeanScope {
        SINGLETON,
        PROTOTYPE,
        REQUEST,
        SESSION,
        GLOBAL_SESSION

    }

    public BeanScope fromString(String scope) {
        if ("prototype".equals(scope)) {
            return BeanScope.PROTOTYPE;
        } else if ("singleton".equals(scope)) {
            return BeanScope.SINGLETON;
        } else if ("request".equals(scope)) {
            return BeanScope.REQUEST;
        } else if ("session".equals(scope)) {
            return BeanScope.SESSION;
        } else {
            return BeanScope.GLOBAL_SESSION;
        }
    }

    static public class ConstructorArgument {
        private CtClass<?> type;
        private String name;
        private String value;
    }

    private String name;
    private CtClass<?> type;
    private BeanScope scope = BeanScope.SINGLETON;
    private List<ConstructorArgument> constructorArguments;
    private CtMethod<?> initializeMethod;
    private CtConstructor<?> constructor;
    private List<CtField<?>> properties;
    private boolean lazyInit = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CtClass<?> getType() {
        return type;
    }

    public void setType(CtClass<?> type) {
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

    @Override
    public String toString() {
        return "BeanDefinitionModel{" +
                "name=" + (name != null ? name : "")  +
                ", type=" + type.getQualifiedName() +
                ", scope=" + scope +
                ", constructorArguments=" + constructorArguments +
                ", initializeMethod=" + initializeMethod +
                ", constructor=" + constructor +
                ", properties=" + properties +
                ", lazyInit=" + lazyInit +
                '}';
    }
}
