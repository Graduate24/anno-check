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

    public CtLiteral<?> getValueOfAnnotation(CtElement element, String annotationName) {
        return getValueOfAnnotation(element, annotationName, VALUE);
    }

    public CtLiteral<?> getValueOfAnnotation(CtElement element, String annotationName, String key) {
        final CtLiteral<?>[] result = new CtLiteral[1];
        element.getAnnotations().stream().filter(v -> v.getAnnotationType().getQualifiedName()
                        .equals(annotationName))
                .findFirst().ifPresent(o -> {
                    boolean v = ((CtAnnotationImpl<?>) o).getElementValues().containsKey(key);
                    if (v) {
                        var exp = o.getValue(key);
                        if (exp instanceof CtLiteral<?> literal) {
                            result[0] = literal;
                        }
                    }
                });
        return result[0];
    }

    public String getValueOfAnnotationAsString(CtElement element, String annotationName) {
        return getValueOfAnnotationAsString(element, annotationName, VALUE);
    }

    public String getValueOfAnnotationAsString(CtElement element, String annotationName, String key) {
        var anno = getValueOfAnnotation(element, annotationName, key);
        return anno == null ? null : (String) anno.getValue();
    }

    public Boolean getValueOfAnnotationAsBoolean(CtElement element, String annotationName) {
        var anno = getValueOfAnnotation(element, annotationName);
        return anno == null ? null : (Boolean) anno.getValue();
    }

    public String getAnnoValue(CtElement element, String anno) {
        return getAnnoValue(element, anno, VALUE);
    }

    public String getAnnoValue(CtElement element, String anno, String key) {
        return getValueOfAnnotationAsString(element, anno, key);
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
        CtType<?> clazz = (CtType<?>) element;
        return clazz.getFields();
    }

    public String defaultBeanNameFromClass(CtElement element) {
        CtType<?> clazz = (CtType<?>) element;
        String name = clazz.getSimpleName();

        char[] cs = name.toCharArray();
        if (cs[0] >= 65 && cs[0] <= 90) {
            cs[0] += 32;
        }
        return String.valueOf(cs);
    }

    public Map<String, Object> valueAnnoFieldValue(List<CtField<?>> fields) {
        if (fields == null || fields.isEmpty()) {
            return null;
        }
        Map<String, Object> values = new HashMap<>();

        fields.forEach(f -> {
            String value = getAnnoValue(f, VALUE_ANNO);
            // TODO only handle "${ }" placeholder for now
            if (value != null) {
                String keys = getValue(value);
                if (keys != null) {
                    var v = ModelFactory.getConfigFromName(keys);
                    if (v != null) {
                        values.put(f.getSimpleName(), v);
                    }
                }
            }
        });
        return values;
    }

    public Map<String, Object> valueAnnoFieldValue(List<CtField<?>> fields, String prefix) {
        if (fields == null || fields.isEmpty()) {
            return null;
        }
        Map<String, Object> values = new HashMap<>();

        fields.forEach(f -> {
            var v = ModelFactory.getConfigFromName(prefix == null ? f.getSimpleName() : prefix + "." + f.getSimpleName());
            if (v != null) {
                values.put(f.getSimpleName(), v);
            }
        });
        return values;
    }

    public String getValue(String input) {
        String regex = "\\$\\{([^}]+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


}
