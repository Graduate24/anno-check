import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public class TestXmlResource {
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

    @Test
    public void test1() {
        String project = "D:\\edgedownload\\mall-master";
        Map<String, String> mappers = findMyBatisMappers(project);
        mappers.forEach((path, namespace) -> System.out.println("Mapper: " + path + ", Namespace: " + namespace));
    }

}
