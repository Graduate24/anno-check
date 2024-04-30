package analysis.processor.ioc.linker;

import analysis.processor.ioc.beanloader.BeanDefinitionModel;
import analysis.processor.ioc.beanregistor.BeanRegister;
import resource.ModelFactory;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 * <p>
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 *//**
 * Created by: zhang ran
 * 2024-03-18
 */

/**
 * Created by: zhang ran
 * 2024-03-18
 */
public class SpringValueAnnoFieldLinker implements Linker {

    private final Map<CtElement, Set<BeanDefinitionModel>> link = new HashMap<>();

    private static final String VALUE_ANNOTATION = "org.springframework.beans.factory.annotation.Value";

    @Override
    public void link(CtElement element) {
        CtField<?> field = (CtField<?>) element;
        var o = getAnnoValue(element, VALUE_ANNOTATION);
        if (o == null) return;
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
        beanName = get$placeholder(o);
        if (beanName != null) {
            var obj = ModelFactory.getConfigFromName(beanName);
            if (obj != null) {
                BeanDefinitionModel bd = new BeanDefinitionModel();
                bd.setName(beanName);
                bd.setType(((CtField<?>) element).getType().getTypeDeclaration());
                bd.setFromSource(BeanDefinitionModel.FromSource.VALUE_ANNOTATION);
                Map<String, Object> pv = new HashMap<>();
                pv.put(field.getSimpleName(), obj);
                bd.setPropertyValue(pv);
                link.put(element, Collections.singleton(bd));
            }
            return;
        }

//        var bs = BeanRegister.getBeanByName(field.getType().getQualifiedName() + "#" + field.getSimpleName());
//        if (bs != null) {
//            link.put(element, bs);
//        }
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
