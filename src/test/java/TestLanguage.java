import analysis.lang.interpreter.Interpreter;
import analysis.lang.parser.Parser;
import analysis.lang.parser.Scanner;
import analysis.lang.parser.Stmt;
import analysis.lang.parser.Token;
import org.junit.Test;

import java.util.List;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class TestLanguage extends BaseTest{

    @Test
    public void test1() {
//        String project = "D:\\edgedownload\\mall-master";
        String project = "src/test/resources/demo/";
        getResource(project);
        String[] patterns = {
                "@def f1:filme(* set*(..));",
                "@def f1:filme(* set*(..));" +
                        "@run f1 -> stdout;",
                "@run filme(* com.xyz.service..*.*(java.lang.String, int)) && filme(public void " +
                        "edu.tsinghua.demo.aop.ShipmentService.outerCheck()) " +
                        "||(filme(* set*(..))&& (!filme( public * *(..)  )) )-> \"output/asdf\";",


                "@def f1: filanno(edu.tsinghua.demo.aop.Log);" +
                        "@def f2: f1||filme(public * edu.tsinghua.demo.aop.MathCalculator.add(..));" +
                        "@run f2 -> \"output/demo\";"
        };
        for (String pattern : patterns) {
            System.out.println(pattern);
            Scanner scanner = new Scanner(pattern);
            assert !scanner.isHasError();
            List<Token> tokens = scanner.scanTokens();
            Parser parser = new Parser(tokens);
            var stmts = parser.parse();
            assert !parser.isHasError();
            Interpreter interpreter = new Interpreter();
            interpreter.interpret(stmts);
        }
    }
}
