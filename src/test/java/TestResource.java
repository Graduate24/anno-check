import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import testresource.ResourceScanner;
import testresource.SpringMainClassCollector;

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
        processor.scan(launcher.getModel().getRootPackage());
        c1.elements().forEach(e->{
            System.out.println(e.getQualifiedName());
        });
    }
}
