import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static analysis.SimpleIntegration.analysis;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class TestIntegration {

    @Test
    public void test1() throws IOException {
        var base = "/home/ran/Documents/work/graduate/testproject/";
        var projects = List.of(
                "mall-master",
                "mall4cloud-master",
                "eladmin-master",
                "novel-master",
                "opsli-boot-master",
                "zheng-master",
                "WukongCRM-11.0-JAVA-master",
                "newbee-mall-master",
                "xboot-master/xboot-module/xboot-core"
        );
        /**
         * eladmin-master
         * ---------------------------------------------------------------------------------
         * Language                       files          blank        comment           code
         * ---------------------------------------------------------------------------------
         * Java                             276           2304           8226          12127
         *
         * litemall-master
         * -------------------------------------------------------------------------------
         * Language                     files          blank        comment           code
         * -------------------------------------------------------------------------------
         * Java                           327          14934          40871          60517
         *
         * mall4cloud-master
         * -------------------------------------------------------------------------------
         * Language                     files          blank        comment           code
         * -------------------------------------------------------------------------------
         * Java                           520           8224           8922          27288
         *
         * mall-master
         * -------------------------------------------------------------------------------
         * Language                     files          blank        comment           code
         * -------------------------------------------------------------------------------
         * Java                           526          16820           3079          65687
         *
         * newbee-mall-master
         * -------------------------------------------------------------------------------
         * Language                     files          blank        comment           code
         * -------------------------------------------------------------------------------
         * Java                            88           1215           1457           4937
         *
         * novel-master
         * ----------------------------------------------------------------------------------------
         * Language                              files          blank        comment           code
         * ----------------------------------------------------------------------------------------
         * Java                                    182           2182           3468           7000
         *
         * WukongCRM-11.0-JAVA-master
         * -------------------------------------------------------------------------------
         * Language                     files          blank        comment           code
         * -------------------------------------------------------------------------------
         * Java                          1672          18462          20392          90933
         *
         * zheng-master
         * ----------------------------------------------------------------------------------------
         * Language                              files          blank        comment           code
         * ----------------------------------------------------------------------------------------
         * Java                                    379           7814           3447          30363
         *
         *
         *
         *
         */
//        String project = "D:\\edgedownload\\mall-master"; //1 15s
//        String project = "D:/edgedownload/mall4cloud-master"; //3 10s
//        String project = "src/test/resources/demo/"; // 2 1s
//        String project = "D:/edgedownload/eladmin-master"; //4 5s
//        String project = "D:/edgedownload/mall-swarm-master//mall-portal"; //5
//        String project = "D:\\edgedownload\\jetlinks-community-master"; //6 15s
//        String project = "D:\\edgedownload\\novel-master";
//        String project = "D:\\edgedownload\\ruoyi-vue-pro-master";
//        String project = "D:\\edgedownload\\litemall-master";
//        String project = "D:\\edgedownload\\zheng-master";
        for (String project : projects) {
            project = base + project;
            Path dirPath = Paths.get(project).getFileName();
            String output = "output" + "/" + dirPath; // 10s
            long start = System.currentTimeMillis();
            analysis(project, output);
            long timeSpent = (System.currentTimeMillis() - start) / 1000;
            System.out.println("Time spent: " + timeSpent);
        }

    }

    @Test
    public void testsingle() throws IOException {
        String project = "/home/ran/Documents/work/graduate/BenchmarkJava/annotated-benchmark";
        Path dirPath = Paths.get(project).getFileName();
        String output = "output" + "/" + dirPath; // 10s
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
