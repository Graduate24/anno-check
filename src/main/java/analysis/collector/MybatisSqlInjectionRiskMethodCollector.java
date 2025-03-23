package analysis.collector;

import resource.ResourceRole;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * 用于收集可能存在SQL注入风险的MyBatis和Spring Data JPA注解方法
 * 主要检测使用了${} 而不是 #{} 的方法，以及使用了非参数化查询的方法
 * <p>
 * Created by: zhang ran
 * 2025-03-23
 */
public class MybatisSqlInjectionRiskMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {

    // MyBatis SQL注解列表
    private static final List<String> MYBATIS_SQL_ANNOTATIONS = Arrays.asList(
            "org.apache.ibatis.annotations.Select",
            "org.apache.ibatis.annotations.Insert",
            "org.apache.ibatis.annotations.Update",
            "org.apache.ibatis.annotations.Delete"
    );

    // Spring Data JPA SQL注解列表
    private static final List<String> SPRING_DATA_JPA_ANNOTATIONS = Arrays.asList(
            "org.springframework.data.jpa.repository.Query",
            "org.springframework.data.jpa.repository.Modifying",
            "org.springframework.data.jdbc.repository.query.Query",
            "org.springframework.data.jdbc.repository.query.Modifying"
    );

    // JPQL/HQL风险模式
    private static final List<String> JPA_RISK_PATTERNS = Arrays.asList(
            "${",
            "?#{",
            ":#{",
            "||",
            "CONCAT(",
            "+ '",
            "' +"
    );

    @Override
    public Predicate<E> defaultPredictor() {
        return (E e) -> {
            if (!(e instanceof CtMethod<?> method)) {
                return false;
            }

            for (CtAnnotation<? extends Annotation> annotation : method.getAnnotations()) {
                String annoType = annotation.getAnnotationType().getQualifiedName();

                // 检查MyBatis注解
                if (MYBATIS_SQL_ANNOTATIONS.contains(annoType)) {
                    if (containsRiskPattern(annotation, "${")) {
                        return true;
                    }
                }

                // 检查Spring Data JPA注解
                if (SPRING_DATA_JPA_ANNOTATIONS.contains(annoType)) {
                    if (isJpaQueryWithRisk(annotation)) {
                        return true;
                    }
                }

                // 检查方法名是否是JPA命名查询且存在风险
                if (isJpaMethodNameWithRisk(method)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 检查注解值是否包含指定的风险模式
     *
     * @param annotation  要检查的注解
     * @param riskPattern 风险模式
     * @return 如果包含风险模式则返回true
     */
    private boolean containsRiskPattern(CtAnnotation<? extends Annotation> annotation, String riskPattern) {
        for (Object value : annotation.getValues().values()) {
            if (value instanceof CtLiteral<?>) {
                String sqlValue = ((CtLiteral<?>) value).getValue().toString();
                if (sqlValue.contains(riskPattern)) {
                    return true;
                }
            } else if (value instanceof String sqlValue) {
                if (sqlValue.contains(riskPattern)) {
                    return true;
                }
            } else if (value instanceof String[]) {
                for (String sqlValue : (String[]) value) {
                    if (sqlValue.contains(riskPattern)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查JPA Query注解是否存在SQL注入风险
     *
     * @param annotation JPA Query注解
     * @return 如果存在风险则返回true
     */
    private boolean isJpaQueryWithRisk(CtAnnotation<? extends Annotation> annotation) {
        for (String riskPattern : JPA_RISK_PATTERNS) {
            if (containsRiskPattern(annotation, riskPattern)) {
                return true;
            }
        }

        // 检查nativeQuery=true且包含参数拼接的情况
        Boolean isNativeQuery = false;
        for (String key : annotation.getValues().keySet()) {
            if (key.equals("nativeQuery")) {
                Object value = annotation.getValues().get(key);
                if (value instanceof CtLiteral<?>) {
                    isNativeQuery = (Boolean) ((CtLiteral<?>) value).getValue();
                }
            }
        }

        if (isNativeQuery) {
            // 对于原生SQL查询，检查是否有直接字符串拼接的风险
            String query = "";
            if (annotation.getValues().containsKey("value")) {
                Object value = annotation.getValues().get("value");
                if (value instanceof CtLiteral<?>) {
                    query = ((CtLiteral<?>) value).getValue().toString();
                } else if (value instanceof String) {
                    query = (String) value;
                }
            }

            // 检查原生SQL中的风险模式，如字符串拼接
            return query.contains("${") || query.contains("+ '") || query.contains("' +");
        }

        return false;
    }

    /**
     * 检查JPA方法名查询是否存在SQL注入风险
     * 例如：findUserByName(String name) 方法，如果方法体中有风险代码
     *
     * @param method 方法
     * @return 如果存在风险则返回true
     */
    private boolean isJpaMethodNameWithRisk(CtMethod<?> method) {
        String methodName = method.getSimpleName();

        // 检查方法名是否是JPA命名查询格式
        if (methodName.startsWith("findBy") || methodName.startsWith("getBy") ||
                methodName.startsWith("readBy") || methodName.startsWith("queryBy") ||
                methodName.startsWith("searchBy") || methodName.startsWith("deleteBy") ||
                methodName.startsWith("removeBy") || methodName.startsWith("countBy")) {

            // 检查方法体是否存在，以及是否包含风险代码
            if (method.getBody() != null) {
                String bodyCode = method.getBody().toString();
                for (String riskPattern : JPA_RISK_PATTERNS) {
                    if (bodyCode.contains(riskPattern)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }
}
