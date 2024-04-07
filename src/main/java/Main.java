import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;

import static analysis.SimpleIntegration.analysis;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // TODO
        System.out.println("Anno Check");
        ArgumentParser parser = ArgumentParsers.newFor("ac").build()
                .defaultHelp(true)
                .description("Run analysis of given project. Example: -p /project -o output/result");

        parser.addArgument("-p", "--project")
                .help("Project to be analysis. A directory path.");
        parser.addArgument("-o", "--output")
                .help("Out put file path. Set 'stdout' will print result in console. ");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        System.out.println("Start analysis...");

        String project = ns.getString("project");
        if (project != null) {
            System.out.println("project: " + project);
        } else {
            System.out.println("project not set");
            System.exit(65);
        }


        String output = ns.getString("output");
        if (output != null) {
            System.out.println("output: " + output);
        } else {
            output = "stdout";
        }

        analysis(project, output);
    }


}
