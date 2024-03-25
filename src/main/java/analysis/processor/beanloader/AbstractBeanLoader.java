package analysis.processor.beanloader;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.List;

public abstract class AbstractBeanLoader implements BeanLoader {
    protected final static String COMPONENT = "org.springframework.stereotype.Component";
    protected final static String SERVICE = "org.springframework.stereotype.Service";
    protected final static String CONTROLLER = "org.springframework.stereotype.Controller";
    protected final static String SCOPE = "org.springframework.context.annotation.Scope";
    protected final static String LAZY = "org.springframework.context.annotation.Lazy";
    protected final static String QUALIFIER = "org.springframework.beans.factory.annotation.Qualifier";
    protected final static String BEAN = "org.springframework.context.annotation.Bean";
    protected final static String VALUE = "value";

    public CtLiteral<?> getValueOfAnnotation(CtElement element, String annotationName) {
        final CtLiteral<?>[] result = new CtLiteral[1];
        element.getAnnotations().stream().filter(v -> v.getAnnotationType().getQualifiedName()
                        .equals(annotationName))
                .findFirst().ifPresent(o -> {
                    boolean v = ((CtAnnotationImpl<?>) o).getElementValues().containsKey(VALUE);
                    if (v) {
                        var exp = o.getValue(VALUE);
                        if (exp instanceof CtLiteral<?> literal) {
                            result[0] = literal;
                        }
                    }
                });
        return result[0];
    }

    public String getValueOfAnnotationAsString(CtElement element, String annotationName) {
        var anno = getValueOfAnnotation(element, annotationName);
        return anno == null ? null : (String) anno.getValue();
    }

    public Boolean getValueOfAnnotationAsBoolean(CtElement element, String annotationName) {
        var anno = getValueOfAnnotation(element, annotationName);
        return anno == null ? null : (Boolean) anno.getValue();
    }

    public String getAnnoValue(CtElement element,String anno) {
        return getValueOfAnnotationAsString(element, anno);
    }

    public String getScopeValue(CtElement element) {
        return getValueOfAnnotationAsString(element, SCOPE);
    }

    public boolean getLazyValue(CtElement element) {
        var value = getValueOfAnnotationAsBoolean(element, LAZY);
        return value != null && value;
    }

    public CtConstructor<?> getConstructorWithNoParams(CtElement element) {
        CtClass<?> clazz = (CtClass<?>) element;
        final CtConstructor<?>[] result = new CtConstructor<?>[1];
        clazz.getConstructors().stream()
                .filter(constructor -> constructor.getParameters().isEmpty())
                .findFirst()
                .ifPresent(defaultConstructor ->
                        result[0] = defaultConstructor
                );
        return result[0];
    }

    public List<CtField<?>> getFields(CtElement element) {
        CtClass<?> clazz = (CtClass<?>) element;
        return clazz.getFields();
    }

    public String defaultBeanNameFromClass(CtElement element) {
        CtClass<?> clazz = (CtClass<?>) element;
        String name = clazz.getSimpleName();

        char[] cs = name.toCharArray();
        if (cs[0] >= 65 && cs[0] <= 90) {
            cs[0] += 32;
        }
        return String.valueOf(cs);
    }


}
