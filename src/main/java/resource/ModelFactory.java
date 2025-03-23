package resource;

import analysis.processor.ioc.beanregistor.IoCContainerModel;
import org.yaml.snakeyaml.Yaml;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static tool.MybatisSqlExtractor.extractSqlInjectionRiskMethods;
import static tool.MybatisSqlExtractor.getNamespace;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class ModelFactory {
    private static CtModel model;
    private static String filePath;

    private static Set<String> mapper;
    // may probably cause sql injection
    private static Set<String> xmlSqlInjectionMethods;

    private static Map<String, Object> config;

    public static CtModel init(String path) {
        if (model == null) {
            Launcher launcher = new Launcher();
            launcher.addInputResource(path);
            model = launcher.buildModel();
            filePath = path;
        }
        if (mapper == null || xmlSqlInjectionMethods == null) {
            mapper = new HashSet<>();
            xmlSqlInjectionMethods = new HashSet<>();
            findMyBatisMappers(ModelFactory.getFilePath());
        }
        if (config == null) {
            config = findYamlConfig(path);
        }

        return model;
    }

    public static void reset() {
        model = null;
        filePath = null;
        mapper = null;
        config = null;
        CachedElementFinder.reset();
        IoCContainerModel.INSTANCE.reset();
    }

    public static CtModel getModel() {
        return model;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static Set<String> getMybatisMapper() {
        return mapper;
    }

    public static Set<String> getXmlSqlInjectionMethods() {
        return xmlSqlInjectionMethods;
    }

    public static Map<String, Object> getConfig() {
        return config;
    }


    private static void findMyBatisMappers(String directoryPath) {
        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".xml"))
                    .forEach(path -> {
                        try {
                            mapper.add(getNamespace(path.toFile()));
                            List<String> methodPaths = extractSqlInjectionRiskMethods(path.toFile());
                            xmlSqlInjectionMethods.addAll(methodPaths);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> findYamlConfig(String directoryPath) {
        Map<String, Object> yamlConfig = new HashMap<>();
        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".yml")
                            || path.toString().toLowerCase().endsWith("yaml"))
                    .forEach(path -> {
                        try {
                            // 首先读取文件内容
                            String content = new String(Files.readAllBytes(path));
                            
                            // 预处理内容，将@xxx@格式的值替换为引号包裹的字符串
                            content = content.replaceAll("\\s+(@[^@\\s]+@)\\s*$", " '$1'");
                            content = content.replaceAll(":\\s+(@[^@\\s]+@)\\s*$", ": '$1'");
                            
                            // 使用处理后的内容创建YAML解析器
                            Yaml yaml = new Yaml();
                            try {
                                yaml.loadAll(content).forEach(o -> {
                                    if (o instanceof Map) {
                                        Map<String, Object> obj = (Map<String, Object>) o;
                                        yamlConfig.putAll(obj);
                                    }
                                });
                            } catch (Exception e) {
                                // 如果解析仍然失败，尝试更保守的方式：忽略包含@的行
                                String[] lines = content.split("\\r?\\n");
                                StringBuilder filteredContent = new StringBuilder();
                                for (String line : lines) {
                                    if (!line.contains("@")) {
                                        filteredContent.append(line).append("\n");
                                    }
                                }
                                
                                try {
                                    yaml.loadAll(filteredContent.toString()).forEach(o -> {
                                        if (o instanceof Map) {
                                            Map<String, Object> obj = (Map<String, Object>) o;
                                            yamlConfig.putAll(obj);
                                        }
                                    });
                                } catch (Exception innerEx) {
                                    System.err.println("无法解析YAML文件（甚至在忽略@符号后）: " + path);
                                    innerEx.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("处理YAML文件时出错: " + path);
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yamlConfig;
    }

    public static Object getConfigFromName(String name) {
        String[] keys = name.split("\\.");
        Map<String, Object> m = config;
        Object result = null;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            var v = m.get(key);
            if (v instanceof Map) {
                m = (Map<String, Object>) v;
            } else {
                if (i == keys.length - 1) {
                    result = v;
                }
                break;
            }
        }
        return result;
    }
}
