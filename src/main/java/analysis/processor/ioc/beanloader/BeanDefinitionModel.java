package analysis.processor.ioc.beanloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import spoon.reflect.declaration.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 获取构造函数参数的JSON表示
        public Map<String, Object> toJsonMap() {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("type", type != null ? type.getQualifiedName() : null);
            jsonMap.put("name", name);
            jsonMap.put("value", value);
            return jsonMap;
        }
    }

    private String name;
    private String type;
    private BeanScope scope = BeanScope.SINGLETON;
    private CtMethod<?> initializeMethod;
    private List<String> constructors;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BeanScope getScope() {
        return scope;
    }

    public void setScope(BeanScope scope) {
        this.scope = scope;
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

    public List<String> getConstructors() {
        return constructors;
    }

    public void setConstructors(List<String> constructors) {
        this.constructors = constructors;
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

    /**
     * 自定义序列化器，处理CtElement的特殊序列化
     */
    private static class BeanDefinitionModelSerializer implements JsonSerializer<BeanDefinitionModel> {
        @Override
        public JsonElement serialize(BeanDefinitionModel src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();

            // 基本属性
            json.addProperty("name", src.name);
            json.addProperty("type", src.type);
            json.addProperty("scope", src.scope != null ? src.scope.name() : null);
            json.addProperty("fromSource", src.fromSource != null ? src.fromSource.name() : null);
            json.addProperty("lazyInit", src.lazyInit);

            // 初始化方法
            if (src.initializeMethod != null) {
                json.addProperty("initializeMethod", src.initializeMethod.getSimpleName());
            }

            // 构造函数
            if (src.constructors != null) {
                json.add("constructors", context.serialize(src.constructors));
            }

            // 属性列表
            if (src.properties != null && !src.properties.isEmpty()) {
                json.add("properties", context.serialize(src.properties.stream()
                        .map(CtNamedElement::getSimpleName)
                        .collect(Collectors.toList())));
            }

            // 属性值
            if (src.propertyValue != null && !src.propertyValue.isEmpty()) {
                json.add("propertyValue", context.serialize(src.propertyValue));
            }

            return json;
        }
    }

    /**
     * 将BeanDefinitionModel对象转换为JSON字符串
     * @return 标准JSON格式的字符串表示
     */
    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BeanDefinitionModel.class, new BeanDefinitionModelSerializer())
                .create();
        return gson.toJson(this);
    }

    /**
     * 将BeanDefinitionModel对象转换为格式化的JSON字符串
     * @return 格式化的标准JSON字符串
     */
    public String toPrettyJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BeanDefinitionModel.class, new BeanDefinitionModelSerializer())
                .setPrettyPrinting()
                .create();
        return gson.toJson(this);
    }

    /**
     * 将BeanDefinitionModel对象转换为Map，便于序列化
     * @return 包含对象属性的Map
     */
    public Map<String, Object> toJsonMap() {
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("name", name);
        jsonMap.put("type", type);
        jsonMap.put("scope", scope != null ? scope.name() : null);
        jsonMap.put("fromSource", fromSource != null ? fromSource.name() : null);


        // 初始化方法
        jsonMap.put("initializeMethod", initializeMethod != null ? initializeMethod.getSimpleName() : null);

        // 构造函数
        jsonMap.put("constructor", constructors);

        // 属性列表
        if (properties != null && !properties.isEmpty()) {
            List<String> propList = properties.stream()
                    .map(CtNamedElement::getSimpleName)
                    .collect(Collectors.toList());
            jsonMap.put("properties", propList);
        } else {
            jsonMap.put("properties", new ArrayList<>());
        }

        // 属性值
        jsonMap.put("propertyValue", propertyValue);

        // 懒加载
        jsonMap.put("lazyInit", lazyInit);

        return jsonMap;
    }

    @Override
    public String toString() {
        return toPrettyJson();
    }
}
