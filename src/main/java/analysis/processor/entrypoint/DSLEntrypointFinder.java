package analysis.processor.entrypoint;

import analysis.lang.interpreter.Interpreter;
import analysis.lang.parser.Parser;
import analysis.lang.parser.Scanner;
import analysis.lang.parser.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class DSLEntrypointFinder {

    public final static String defaultOutput = "-> \"output/entrypoint\";";

    public final static String consoleOutput = "-> stdout;";

    private String projectPath;

    private final static String defaultFilter = "" +
            "@def f1: filmanno(org.springframework.amqp.rabbit.annotation.RabbitHandler);\n" +
            "@def f2: filmanno(org.springframework.web.bind.annotation.Mapping);\n" +
            "@def f3: filmanno(org.springframework.web.bind.annotation.GetMapping);\n" +
            "@def f4: filmanno(org.springframework.web.bind.annotation.PostMapping);\n" +
            "@def f5: filmanno(org.springframework.web.bind.annotation.PutMapping);\n" +
            "@def f6: filmanno(org.springframework.web.bind.annotation.DeleteMapping);\n" +
            "@def f7: filmanno(org.springframework.web.bind.annotation.PatchMapping);\n" +
            "@def f8: filmanno(org.springframework.web.bind.annotation.ExceptionHandler);\n" +
            "@def f9: filmanno(org.springframework.web.bind.annotation.RequestMapping);\n" +
            "@def f10: filme(pubstatic * *(..));\n" +
            "\n" +
            "@run f1||f2||f3||f4||f5||f6||f7||f8||f9||f10 ";

    public DSLEntrypointFinder() {
    }

    public boolean findDefaultOutputConsole() {
        return interpretStmt(consoleOutput);
    }

    public boolean findDefaultOutputFile() {
        return interpretStmt(defaultOutput);
    }

    public boolean findDefaultOutputFile(String filePath) {
        return interpretStmt("-> \"" + filePath + "\";");
    }

    public boolean findByDSL(String dslPath) {
        try (var s = Files.lines(Paths.get(dslPath), StandardCharsets.UTF_8)) {
            String content = s.collect(Collectors.joining(System.lineSeparator()));
            return interpretRaw(content);
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

    private boolean interpretStmt(String defaultOutput) {
        String dsl = defaultFilter + defaultOutput;
        return interpretRaw(dsl);
    }
}
