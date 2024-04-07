import analysis.processor.entrypoint.DSLEntrypointFinder;
import org.junit.Test;
import resource.ProjectResource;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class TestEntrypoint {

    @Test
    public void test1() {
//        String project = "D:\\edgedownload\\mall-master";
        String project = "src/test/resources/demo/";
//        ModelFactory.reset();
//        ModelFactory.init(project);
        ProjectResource.getResource(project);

        DSLEntrypointFinder ep = new DSLEntrypointFinder();
//        ep.findDefaultOutputConsole();
//        ep.findDefaultOutputFile();
        ep.findDefaultOutputFile("output/entry2");

        ep.findByDSL("src/test/resources/dsl/entrypoint");
    }
}
