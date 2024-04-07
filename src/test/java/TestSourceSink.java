import analysis.lang.interpreter.Interpreter;
import analysis.lang.parser.Parser;
import analysis.lang.parser.Scanner;
import analysis.lang.parser.Token;
import org.junit.Test;
import resource.ProjectResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class TestSourceSink {


    @Test
    public void test1() {
        String project = "D:\\edgedownload\\mall-master";
//        String project = "src/test/resources/demo/";
//        ModelFactory.reset();
//        ModelFactory.init(project);
        ProjectResource.getResource(project);


        String dsl = findDSL("src/test/resources/dsl/sourcesink");
        interpretRaw(dsl);
    }

    public String findDSL(String dslPath) {
        try (var s = Files.lines(Paths.get(dslPath), StandardCharsets.UTF_8)) {
            return s.collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean interpretRaw(String dsl) {
        Scanner scanner = new Scanner(dsl);
        if (scanner.isHasError()) {
            return false;
        }
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        var stmts = parser.parse();
        if (parser.isHasError()) {
            return false;
        }
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(stmts);
        return !interpreter.hadRuntimeError;
    }
}
