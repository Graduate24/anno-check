import analysis.collector.SpringMainClassCollector;
import analysis.collector.SpringServiceAnnoClassCollector;
import analysis.processor.ResourceScanner;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

public class TestResource {

    @Test
    public void test1(){
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        launcher.process();
        ResourceScanner processor = new ResourceScanner();
        var c1 = new SpringMainClassCollector<CtClass<?>>();
        processor.addCollector(c1);
        var c2 = new SpringServiceAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c2);
        processor.scan(launcher.getModel().getRootPackage());
        System.out.println("Main class: ");
        c1.elements().forEach(e->{
            System.out.println(e.getQualifiedName());
        });
        System.out.println("Service class: ");
        c2.elements().forEach(e->{
            System.out.println(e.getQualifiedName());
        });
    }
}
