import analysis.collector.SpringBeanAnnoMethodCollector;
import analysis.collector.SpringComponentAnnoClassCollector;
import analysis.collector.SpringControllerClassCollector;
import analysis.collector.SpringServiceAnnoClassCollector;
import analysis.processor.beanloader.*;
import analysis.processor.beanregistor.BeanRegister;
import analysis.processor.beanregistor.IoCContainerModel;
import org.junit.Test;
import resource.ModelFactory;
import resource.JavaResourceScanner;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

public class TestBeanRegister {
    @Test
    public void test1() {
        System.out.println("Test BeanRegister");
    }

    public List<BeanDefinitionModel> collectBeanModel() {
        CtModel model = ModelFactory.init("src/test/resources/demo/src/main/java/");
        JavaResourceScanner processor = new JavaResourceScanner();

        var c1 = new SpringComponentAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c1);

        var c2 = new SpringServiceAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c2);

        var c3 = new SpringControllerClassCollector<CtClass<?>>();
        processor.addCollector(c3);

        var c4 = new SpringBeanAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c4);

        processor.scan(model.getRootPackage());

        var list = new ArrayList<BeanDefinitionModel>();

        var com = new SpringComponentAnnoBeanLoader();
        c1.elements().forEach(e -> {
            var bd = com.load(null, e);
            list.add(bd);
        });

        var ser = new SpringServiceAnnoBeanLoader();
        c2.elements().forEach(e -> {
            var bd = ser.load(null, e);
            list.add(bd);
        });
        var con = new SpringControllerAnnoBeanLoader();
        c3.elements().forEach(e -> {
            var bd = con.load(null, e);
            list.add(bd);
        });

        var be = new SpringBeanAnnoBeanLoader();
        c4.elements().forEach(e -> {
            var bd = be.load(null, e);
            list.add(bd);
        });
        return list;
    }

    @Test
    public void test2() {
        collectBeanModel().forEach(BeanRegister::register);
        IoCContainerModel.INSTANCE.debug();

        var bs = BeanRegister.getBeanByTypeAndSunType("edu.tsinghua.demo.service.demo1.Demo1Service");

        System.out.println(bs);
    }
}
