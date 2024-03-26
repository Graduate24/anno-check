package analysis.processor.linker;

import analysis.processor.beanloader.BeanDefinitionModel;
import analysis.processor.beanregistor.BeanRegister;
import resource.ModelFactory;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringValueAnnoFieldLinker implements Linker {

    private final Map<CtElement, Set<BeanDefinitionModel>> link = new HashMap<>();

    private static final String VALUE_ANNOTATION = "org.springframework.beans.factory.annotation.Value";

    @Override
    public void link(CtElement element) {
        CtField<?> field = (CtField<?>) element;
        var o = getAnnoValue(element, VALUE_ANNOTATION);
//        boolean v = ((CtAnnotationImpl<?>) o).getElementValues().containsKey("required");
//        boolean required = !v || (boolean) ((CtLiteral<?>) o.getValue("required")).getValue();
        var beanName = getExpPlaceholder(o);
        if (beanName != null) {
            var bs = BeanRegister.getBeanByName(beanName);
            if (bs != null) {
                link.put(element, bs);
            }
            return;
        }

        var bs = BeanRegister.getBeanByName(field.getDeclaringType().getQualifiedName() + "#" + field.getSimpleName());
        if (bs != null) {
            link.put(element, bs);
        }
    }

    @Override
    public Set<BeanDefinitionModel> findLink(CtElement element) {
        return link.getOrDefault(element, new HashSet<>());
    }

    public String get$placeholder(String input) {
        String regex = "\\$\\{([^}]+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public String getExpPlaceholder(String input) {
        String regex = "#\\{([^}]+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public CtLiteral<?> getValueOfAnnotation(CtElement element, String annotationName) {
        return getValueOfAnnotation(element, annotationName, "value");
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
        return getValueOfAnnotationAsString(element, annotationName, "value");
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
        return getAnnoValue(element, anno, "value");
    }

    public String getAnnoValue(CtElement element, String anno, String key) {
        return getValueOfAnnotationAsString(element, anno, key);
    }

}
