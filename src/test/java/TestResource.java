import analysis.collector.*;
import analysis.processor.ResourceScanner;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

public class TestResource {

    @Test
    public void test1() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        launcher.process();
        ResourceScanner processor = new ResourceScanner();


        var c1 = new SpringMainClassCollector<CtClass<?>>();
        processor.addCollector(c1);
        var c2 = new SpringServiceAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c2);
        var c3 = new SpringComponentAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c3);
        var c4 = new SpringBeanAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c4);
        var c5 = new SpringValueAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c5);
        var c6 = new SpringRepositoryAnnoInterfaceCollector<CtInterface<?>>();
        processor.addCollector(c6);
        var c7 = new SpringRepositoryMethodCollector<CtMethod<?>>();
        processor.addCollector(c7);

        processor.scan(launcher.getModel().getRootPackage());


        System.out.println("Main class: ");
        c1.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName());
        });
        System.out.println("Service class: ");
        c2.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName());
        });
        System.out.println("Component class: ");
        c3.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName());
        });
        System.out.println("Bean method: ");
        c4.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Value field: ");
        c5.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSimpleName());
        });
        System.out.println("Repository interface: ");
        c6.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName() + "." + e.getSimpleName());
        });
        System.out.println("Repository method: ");
        c7.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSimpleName());
        });
    }
}
