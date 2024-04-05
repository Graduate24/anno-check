import analysis.collector.*;
import analysis.processor.ioc.beanloader.*;
import org.junit.Test;
import resource.JavaResourceScanner;
import resource.ModelFactory;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

public class TestBeanLoader {

    @Test
    public void test1() {
        System.out.println("Test BeanLoader");
    }

    @Test
    public void test2() {
        CtModel model = ModelFactory.init("src/test/resources/demo/");
        JavaResourceScanner processor = new JavaResourceScanner();

        var c1 = new SpringComponentAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c1);

        var c2 = new SpringServiceAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c2);

        var c3 = new SpringControllerClassCollector<CtClass<?>>();
        processor.addCollector(c3);

        var c4 = new SpringBeanAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c4);

        var c5 = new SpringConfigurationPropertiesClassCollector<CtClass<?>>();
        processor.addCollector(c5);

        var c6 = new SpringConfigurationClassCollector<CtClass<?>>();
        processor.addCollector(c6);

        var c7 = new SpringValueAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c7);

        processor.scan(model.getRootPackage());

        var com = new SpringComponentAnnoBeanLoader();
        System.out.println("@Component");
        c1.elements().forEach(e -> {
            var bd = com.load(null, e);
            System.out.println(bd);
        });
        System.out.println();

        System.out.println("@Service");
        var ser = new SpringServiceAnnoBeanLoader();
        c2.elements().forEach(e -> {
            var bd = ser.load(null, e);
            System.out.println(bd);
        });
        System.out.println();
        System.out.println("@Controller");
        var con = new SpringControllerAnnoBeanLoader();
        c3.elements().forEach(e -> {
            var bd = con.load(null, e);
            System.out.println(bd);
        });

        System.out.println();
        System.out.println("@Bean");
        var be = new SpringBeanAnnoBeanLoader();
        c4.elements().forEach(e -> {
            var bd = be.load(null, e);
            System.out.println(bd);
        });

        System.out.println();
        System.out.println("@ConfigurationProperties");
        var cp = new SpringConfigurationPropertiesBeanLoader();
        c5.elements().forEach(e -> {
            var bd = cp.load(null, e);
            System.out.println(bd);
        });

        System.out.println();
        System.out.println("@Configuration");
        var c = new SpringConfigurationBeanLoader();
        c6.elements().forEach(e -> {
            var bd = c.load(null, e);
            System.out.println(bd);
        });

        System.out.println();
        System.out.println("@Value");
        var v = new SpringValueAnnoBeanLoader();
        c7.elements().forEach(e -> {
            var bd = v.load(null, e);
            System.out.println(bd);
        });

    }
}
