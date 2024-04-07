import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class TestCMD {
    @Test
    public void test1() throws IOException {
        // java17
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String template = "{0} -jar {1} {2}";
        String cmd = MessageFormat.format(template, javaBin, "./target/anno-check.jar", "-h");
        System.out.println("cmd: " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader in =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        String inputLine;
        System.out.println("result: ");
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }
        in.close();
    }

    @Test
    public void test2() throws IOException {
        // java17
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String template = "{0} -jar {1} -p {2} -o {3}";
//        String project = "D:\\edgedownload\\mall-master";
        String project = "src/test/resources/demo/";
        String output = "output/report.txt";
        String cmd = MessageFormat.format(template, javaBin, "./target/anno-check.jar", project, output);
        System.out.println("cmd: " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader in =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        String inputLine;
        System.out.println("result: ");
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }
        in.close();
    }
}
