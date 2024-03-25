import analysis.collector.*;
import analysis.processor.beanloader.*;
import analysis.processor.beanregistor.BeanRegister;
import analysis.processor.beanregistor.IoCContainerModel;
import analysis.processor.linker.SpringAutowiredAnnoFieldLinker;
import org.junit.Test;
import resource.ModelFactory;
import resource.ResourceScanner;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestBeanLinker {
    public List<BeanDefinitionModel> collectBeanModel() {
        CtModel model = ModelFactory.init("src/test/resources/demo/src/main/java/");
        ResourceScanner processor = new ResourceScanner();

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
    public void test1() {
        // load bean and register bean
        collectBeanModel().forEach(BeanRegister::register);

        // collect @Autowired fields
        ResourceScanner processor = new ResourceScanner();
        var c1 = new SpringAutowiredAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c1);
        processor.scan(ModelFactory.getModel().getRootPackage());

        var linker = new SpringAutowiredAnnoFieldLinker();
        // link
        c1.elements().forEach(e -> {
            linker.link(e);
            var bs = linker.findLink(e);
            System.out.println(e.getDeclaringType().getQualifiedName() + "#" + e.getSimpleName() + "\n     -->  " +
                    bs.size() + "| " + bs.stream().map(b -> b.getType().getQualifiedName()).toList());
        });


    }
}
