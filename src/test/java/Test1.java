import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;

public class Test1 {
    @Test
    public void test1() {
        CtClass<?> l = Launcher.parseClass("class A { @Service()void m() { System.out.println(\"yeah\");} }");
        System.out.println(l.prettyprint());
        l.getMethods().forEach(System.out::print);
    }
}
