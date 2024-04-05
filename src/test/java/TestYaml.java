import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public class TestYaml {

    public Object getConfigFromName(Map<String, Object> obj, String name) {
        String[] keys = name.split("\\.");
        Map<String, Object> m = obj;
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

    @Test
    public void test1() throws FileNotFoundException {
        String path = "src/test/resources/demo/src/main/resources/application-dev.yml";
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(path);
        Map<String, Object> obj = yaml.load(inputStream);
        System.out.println(obj);
        System.out.println(getConfigFromName(obj, "mail.hostname"));
        System.out.println(getConfigFromName(obj, "mail"));
    }

    @Test
    public void test2() {
        // Path to the .properties file
        String propertiesFilePath = "src/test/resources/demo/src/main/resources/p.properties";

        // Load properties from file
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        // Convert Properties to Map<String, String>
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }

        // Use the map
        System.out.println(map);
    }
}
