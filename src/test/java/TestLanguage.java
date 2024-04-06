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
public class TestLanguage {

    @Test
    public void test1() {
        String[] patterns = {
                "@def f1:execution(* set*(..));",
                "@def f1:execution(* set*(..));" +
                        "@run f1() -> out;",
                "@run execution(* com.xyz.service..*.*(java.lang.String, int)) && execution(public void " +
                        "edu.tsinghua.demo.aop.ShipmentService.outerCheck()) " +
                        "||(execution(* set*(..))&& (!execution( public * *(..)  )) )-> output;",
                "@def f1: @annotation(auditable.asdf) || within(com.xyz.service..*.*);" +
                        "@def f2: f1()||execution(* com.xyz.service..*.*Set*Id*(..));" +
                        "@run f2();"
        };
        for (String pattern : patterns) {
            System.out.println(pattern);
            Scanner scanner = new Scanner(pattern);
            List<Token> tokens = scanner.scanTokens();
            Parser parser = new Parser(tokens);
            var stmts = parser.parse();
            assert !parser.isHasError();
            for (Stmt stmt : stmts) {
                System.out.print(stmt.getClass().getSimpleName() + " :");
                System.out.println(stmt);
                System.out.println();
            }
        }
    }
}
