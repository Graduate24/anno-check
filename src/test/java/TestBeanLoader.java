import analysis.collector.SpringComponentAnnoClassCollector;
import analysis.processor.beanloader.SpringComponentAnnoBeanLoader;
import analysis.processor.resource.ResourceScanner;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.HashSet;
import java.util.Set;

public class TestBeanLoader {

    @Test
    public void test1() {
        System.out.println("Test BeanLoader");
    }

    @Test
    public void test2() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        launcher.process();
        ResourceScanner processor = new ResourceScanner();

        var c1 = new SpringComponentAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c1);

        processor.scan(launcher.getModel().getRootPackage());

        Set<CtElement> contextResources = new HashSet<>(c1.elements());
        var com = new SpringComponentAnnoBeanLoader();
        c1.elements().forEach(e -> {
            var bd = com.load(contextResources, e);
            System.out.println(bd);
        });

    }
}
