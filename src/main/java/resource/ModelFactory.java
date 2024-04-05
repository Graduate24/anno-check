package resource;

import org.w3c.dom.Document;
import org.yaml.snakeyaml.Yaml;
import spoon.Launcher;
import spoon.reflect.CtModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Created by: zhang ran
 * 2024-03-05
 * <p>
 * Created by: zhang ran
 * 2024-03-05
 * <p>
 * Created by: zhang ran
 * 2024-03-05
 * <p>
 * Created by: zhang ran
 * 2024-03-05
 * <p>
 * Created by: zhang ran
 * 2024-03-05
 * <p>
 * Created by: zhang ran
 * 2024-03-05
 *//**
 * Created by: zhang ran
 * 2024-03-05
 *//**
 * Created by: zhang ran
 * 2024-03-05
 *//**
 * Created by: zhang ran
 * 2024-03-05
 *//**
 * Created by: zhang ran
 * 2024-03-05
 *//**
 * Created by: zhang ran
 * 2024-03-05
 */

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class ModelFactory {
    private static CtModel model;
    private static String filePath;

    private static Set<String> mapper;

    private static Map<String, Object> config;

    public static CtModel init(String path) {
        if (model == null) {
            Launcher launcher = new Launcher();
            launcher.addInputResource(path);
            model = launcher.buildModel();
            filePath = path;
        }
        if (mapper == null) {
            mapper = new HashSet<>();
            Map<String, String> mappers = findMyBatisMappers(ModelFactory.getFilePath());
            mappers.forEach((p, namespace) -> mapper.add(namespace));
        }
        if (config == null) {
            config = findYamlConfig(path);
        }

        return model;
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

    public static Map<String, Object> getConfig() {
        return config;
    }


    private static Map<String, String> findMyBatisMappers(String directoryPath) {
        Map<String, String> mapperNamespaces = new HashMap<>();
        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".xml"))
                    .forEach(path -> {
                        try {
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                            Document doc = dBuilder.parse(path.toFile());
                            doc.getDocumentElement().normalize();

                            if ("mapper".equals(doc.getDocumentElement().getNodeName())) {
                                String namespace = doc.getDocumentElement().getAttribute("namespace");
                                mapperNamespaces.put(path.toString(), namespace);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapperNamespaces;
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
                            Yaml yaml = new Yaml();
                            InputStream inputStream = new FileInputStream(path.toString());
                            Map<String, Object> obj = yaml.load(inputStream);
                            yamlConfig.putAll(obj);
                        } catch (Exception e) {
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
