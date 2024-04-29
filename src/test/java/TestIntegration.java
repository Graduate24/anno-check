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
//        String project = "D:\\edgedownload\\mall-master"; 1 15s
//        String project = "D:/edgedownload/mall4cloud-master"; //3 10s
//        String project = "src/test/resources/demo/"; // 2 1s
//        String project = "D:/edgedownload/eladmin-master"; //4 5s
//        String project = "D:/edgedownload/mall-swarm-master//mall-admin"; //5
        //String project = "D:\\edgedownload\\jetlinks-community-master"; //6 15s
        String project = "D:\\edgedownload\\novel-master";
        String output = "output/report7"; // 10s
        long start = System.currentTimeMillis();
        analysis(project, output);
        System.out.println("Time spent: "+(System.currentTimeMillis()-start)/1000);
    }
}
