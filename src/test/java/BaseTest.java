import analysis.collector.*;
import resource.JavaResourceScanner;
import resource.ModelFactory;
import resource.ResourceRole;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BaseTest {

    protected List<CtClass<?>> springMainClass = new ArrayList<>();
    protected List<CtClass<?>> springServiceAnnoClass = new ArrayList<>();
    protected List<CtClass<?>> springComponentAnnoClass = new ArrayList<>();
    protected List<CtMethod<?>> springBeanAnnoMethod = new ArrayList<>();
    protected List<CtField<?>> springValueAnnoField = new ArrayList<>();
    protected List<CtInterface<?>> springRepositoryAnnoInterface = new ArrayList<>();
    protected List<CtMethod<?>> springRepositoryMethod = new ArrayList<>();
    protected List<CtField<?>> springAutowiredAnnoField = new ArrayList<>();
    protected List<CtClass<?>> aspectAnnoClass = new ArrayList<>();
    protected List<CtMethod<?>> afterAnnoMethod = new ArrayList<>();
    protected List<CtMethod<?>> afterReturningAnnoMethod = new ArrayList<>();
    protected List<CtMethod<?>> afterThrowingAnnoMethod = new ArrayList<>();
    protected List<CtMethod<?>> aroundAnnoMethod = new ArrayList<>();
    protected List<CtMethod<?>> beforeAnnoMethod = new ArrayList<>();
    protected List<CtMethod<?>> pointcutMethod = new ArrayList<>();
    protected List<CtClass<?>> springConfigurationClass = new ArrayList<>();
    protected List<CtClass<?>> springControllerClass = new ArrayList<>();
    protected List<CtMethod<?>> springMappingMethod = new ArrayList<>();
    protected List<CtMethod<?>> SpringAutowiredAnnoMethod = new ArrayList<>();

    void getResource(String project) {
        CtModel model = ModelFactory.init(project);
        JavaResourceScanner processor = new JavaResourceScanner();

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
        var c8 = new SpringAutowiredAnnoFieldCollector<CtField<?>>();
        processor.addCollector(c8);
        var c9 = new AspectAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c9);
        var c10 = new AfterAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c10);
        var c11 = new AfterReturningAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c11);
        var c12 = new AfterThrowingAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c12);
        var c13 = new AroundAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c13);
        var c14 = new BeforeAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c14);
        var c15 = new PointcutMethodCollector<CtMethod<?>>();
        processor.addCollector(c15);
        var c16 = new SpringConfigurationClassCollector<CtClass<?>>();
        processor.addCollector(c16);
        var c17 = new SpringControllerClassCollector<CtClass<?>>();
        processor.addCollector(c17);
        var c18 = new SpringMappingMethodCollector<CtMethod<?>>();
        processor.addCollector(c18);
        var c19 = new SpringAutowiredAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c19);

        processor.scan(model.getRootPackage());

        springMainClass.addAll(c1.elements());
        springServiceAnnoClass.addAll(c2.elements());
        springComponentAnnoClass.addAll(c3.elements());
        springBeanAnnoMethod.addAll(c4.elements());
        springValueAnnoField.addAll(c5.elements());
        springRepositoryAnnoInterface.addAll(c6.elements());
        springRepositoryMethod.addAll(c7.elements());
        springAutowiredAnnoField.addAll(c8.elements());
        aspectAnnoClass.addAll(c9.elements());
        afterAnnoMethod.addAll(c10.elements());
        afterReturningAnnoMethod.addAll(c11.elements());
        afterThrowingAnnoMethod.addAll(c12.elements());
        aroundAnnoMethod.addAll(c13.elements());
        beforeAnnoMethod.addAll(c14.elements());
        pointcutMethod.addAll(c15.elements());
        springConfigurationClass.addAll(c16.elements());
        springControllerClass.addAll(c17.elements());
        springMappingMethod.addAll(c18.elements());
        SpringAutowiredAnnoMethod.addAll(c19.elements());
    }
}
