import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Map;

public class Test1 {
    @Test
    public void test1() {
        CtClass<?> l = Launcher.parseClass("class A { @Service()void m() { System.out.println(\"yeah\");} }");
        System.out.println(l.toString());
        l.getMethods().forEach(System.out::print);
    }

    @Test
    public void test2(){
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        System.out.println("interfaces: ");
        launcher.getModel().getElements(new TypeFilter<>(CtInterface.class)).forEach(c->{
            System.out.println(c.getSimpleName()+", "+c.getQualifiedName()+", "+c.getSuperInterfaces());
        });
        System.out.println("classes: ");
        launcher.getModel().getElements(new TypeFilter<>(CtClass.class)).forEach(c->{
            System.out.println(c.getSimpleName()+", "+c.getSuperclass()+", "+ c.getSuperInterfaces());
        });

    }
}
