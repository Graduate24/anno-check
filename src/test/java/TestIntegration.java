import org.junit.Test;

import java.io.IOException;

import static analysis.SimpleIntegration.analysis;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class TestIntegration {

    @Test
    public void test1() throws IOException {
        String project = "D:\\edgedownload\\mall-master";
//        String project = "src/test/resources/demo/";
        String output = "output/report";
        analysis(project, output);
    }
}
