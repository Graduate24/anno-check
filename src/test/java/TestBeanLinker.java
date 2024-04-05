import analysis.collector.*;
import analysis.processor.ioc.beanloader.*;
import analysis.processor.ioc.beanregistor.BeanRegister;
import analysis.processor.ioc.linker.SpringAutowiredAnnoFieldLinker;
import analysis.processor.ioc.linker.SpringValueAnnoFieldLinker;
import org.junit.Test;
import resource.JavaResourceScanner;
import resource.ModelFactory;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public class TestBeanLinker {
    public List<BeanDefinitionModel> collectBeanModel(String project) {
        CtModel model = ModelFactory.init(project);
        JavaResourceScanner processor = new JavaResourceScanner();

        var c1 = new SpringComponentAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c1);

        var c2 = new SpringServiceAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c2);

        var c3 = new SpringControllerClassCollector<CtClass<?>>();
        processor.addCollector(c3);

        var c4 = new SpringBeanAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c4);

        var c5 = new SpringRepositoryAnnoInterfaceCollector<CtInterface<?>>();
        processor.addCollector(c5);

        var c6 = new MybatisMapperInterfaceCollector<CtInterface<?>>();
        processor.addCollector(c6);

        var c7 = new SpringMongoRepoInterfaceCollector<CtInterface<?>>();
        processor.addCollector(c7);

        var c8 = new SpringConfigurationPropertiesClassCollector<CtClass<?>>();
        processor.addCollector(c8);

        var c9 = new SpringConfigurationClassCollector<CtClass<?>>();
        processor.addCollector(c9);

        var c10 = new SpringValueAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c10);

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

        var re = new SpringRepositoryAnnoBeanLoader();
        c5.elements().forEach(e -> {
            var bd = re.load(null, e);
            list.add(bd);
        });

        var my = new MybatisMapperBeanLoader();
        c6.elements().forEach(e -> {
            var bd = my.load(null, e);
            list.add(bd);
        });

        var mo = new SpringMongoRepoBeanLoader();
        c7.elements().forEach(e -> {
            var bd = mo.load(null, e);
            list.add(bd);
        });

        var cp = new SpringConfigurationPropertiesBeanLoader();
        c8.elements().forEach(e -> {
            var bd = cp.load(null, e);
            list.add(bd);
        });

        var c = new SpringConfigurationBeanLoader();
        c9.elements().forEach(e -> {
            var bd = c.load(null, e);
            list.add(bd);
        });

        var v = new SpringValueAnnoBeanLoader();
        c10.elements().forEach(e -> {
            var bd = v.load(null, e);
            list.add(bd);
        });

        return list;
    }

    @Test
    public void test1() {
        String project = "D:\\edgedownload\\mall-master";
//        String project = "src/test/resources/demo/";
        // load bean and register bean
        collectBeanModel(project).forEach(BeanRegister::register);

        // collect @Autowired fields
        JavaResourceScanner processor = new JavaResourceScanner();
        var c1 = new SpringAutowiredAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c1);

        var c2 = new SpringValueAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c2);
        processor.scan(ModelFactory.getModel().getRootPackage());

        var linker = new SpringAutowiredAnnoFieldLinker();
        // link
        System.out.println("---------@Autowired-----------\n\n");
        c1.elements().forEach(e -> {
            linker.link(e);
            var bs = linker.findLink(e);
            System.out.println(e.getDeclaringType().getQualifiedName() + "#" + e.getSimpleName() + "\n     -->  " +
                    bs.size() + "| " + bs);
        });

        System.out.println("\n\n---------@Value-----------\n\n");

        var linker2 = new SpringValueAnnoFieldLinker();
        // link
        c2.elements().forEach(e -> {
            linker2.link(e);
            var bs = linker2.findLink(e);
            System.out.println(e.getDeclaringType().getQualifiedName() + "#" + e.getSimpleName() + "\n     -->  " +
                    bs.size() + "| " + bs);
        });


    }
}
