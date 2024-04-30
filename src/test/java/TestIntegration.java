import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static analysis.SimpleIntegration.analysis;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class TestIntegration {

    @Test
    public void test1() throws IOException {
        String project = "D:\\edgedownload\\mall-master"; //1 15s
//        String project = "D:/edgedownload/mall4cloud-master"; //3 10s
//        String project = "src/test/resources/demo/"; // 2 1s
//        String project = "D:/edgedownload/eladmin-master"; //4 5s
//        String project = "D:/edgedownload/mall-swarm-master//mall-portal"; //5
        //String project = "D:\\edgedownload\\jetlinks-community-master"; //6 15s
//        String project = "D:\\edgedownload\\novel-master";
        Path dirPath = Paths.get(project).getFileName();
        String output = "output/" + dirPath + ".report"; // 10s
        long start = System.currentTimeMillis();
        analysis(project, output);
        long timeSpent = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Time spent: " + timeSpent);
    }

    @Test
    public void test2() throws IOException {
        String project = "D:/edgedownload/mall-swarm-master";
        Path dirPath = Paths.get(project);  // Replace with the actual path
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    System.out.println(entry.getFileName());
                    String output = entry.getFileName() + ".report";
                    analysis(entry.toString(), output);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading the directory: " + e.getMessage());
        }

    }

    private static void writeOutput(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Created the directory structure: " + parentDir);
            } else {
                throw new RuntimeException("Failed to create the directory structure: " + parentDir);
            }
        }
        FileWriter writer = new FileWriter(filePath, true);
        writer.write(content);
        writer.close();
    }
}
