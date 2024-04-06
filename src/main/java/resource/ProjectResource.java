package resource;

import analysis.collector.*;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class ProjectResource {
    public static final List<CtClass<?>> springMainClass = new ArrayList<>();
    public static final List<CtClass<?>> springServiceAnnoClass = new ArrayList<>();
    public static final List<CtClass<?>> springComponentAnnoClass = new ArrayList<>();
    public static final List<CtMethod<?>> springBeanAnnoMethod = new ArrayList<>();
    public static final List<CtField<?>> springValueAnnoField = new ArrayList<>();
    public static final List<CtInterface<?>> springRepositoryAnnoInterface = new ArrayList<>();
    public static final List<CtMethod<?>> springRepositoryMethod = new ArrayList<>();
    public static final List<CtField<?>> springAutowiredAnnoField = new ArrayList<>();
    public static final List<CtClass<?>> aspectAnnoClass = new ArrayList<>();
    public static final List<CtMethod<?>> afterAnnoMethod = new ArrayList<>();
    public static final List<CtMethod<?>> afterReturningAnnoMethod = new ArrayList<>();
    public static final List<CtMethod<?>> afterThrowingAnnoMethod = new ArrayList<>();
    public static final List<CtMethod<?>> aroundAnnoMethod = new ArrayList<>();
    public static final List<CtMethod<?>> beforeAnnoMethod = new ArrayList<>();
    public static final List<CtMethod<?>> pointcutMethod = new ArrayList<>();
    public static final List<CtClass<?>> springConfigurationClass = new ArrayList<>();
    public static final List<CtClass<?>> springControllerClass = new ArrayList<>();
    public static final List<CtMethod<?>> springMappingMethod = new ArrayList<>();
    public static final List<CtMethod<?>> SpringAutowiredAnnoMethod = new ArrayList<>();

    public static void getResource(String project) {
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
        springMainClass.clear();
        springMainClass.addAll(c1.elements());
        springServiceAnnoClass.clear();
        springServiceAnnoClass.addAll(c2.elements());
        springComponentAnnoClass.clear();
        springComponentAnnoClass.addAll(c3.elements());
        springBeanAnnoMethod.clear();
        springBeanAnnoMethod.addAll(c4.elements());
        springValueAnnoField.clear();
        springValueAnnoField.addAll(c5.elements());
        springRepositoryAnnoInterface.clear();
        springRepositoryAnnoInterface.addAll(c6.elements());
        springRepositoryMethod.clear();
        springRepositoryMethod.addAll(c7.elements());
        springAutowiredAnnoField.clear();
        springAutowiredAnnoField.addAll(c8.elements());
        aspectAnnoClass.clear();
        aspectAnnoClass.addAll(c9.elements());
        afterAnnoMethod.clear();
        afterAnnoMethod.addAll(c10.elements());
        afterReturningAnnoMethod.clear();
        afterReturningAnnoMethod.addAll(c11.elements());
        afterThrowingAnnoMethod.clear();
        afterThrowingAnnoMethod.addAll(c12.elements());
        aroundAnnoMethod.clear();
        aroundAnnoMethod.addAll(c13.elements());
        beforeAnnoMethod.clear();
        beforeAnnoMethod.addAll(c14.elements());
        pointcutMethod.clear();
        pointcutMethod.addAll(c15.elements());
        springConfigurationClass.clear();
        springConfigurationClass.addAll(c16.elements());
        springControllerClass.clear();
        springControllerClass.addAll(c17.elements());
        springMappingMethod.clear();
        springMappingMethod.addAll(c18.elements());
        SpringAutowiredAnnoMethod.clear();
        SpringAutowiredAnnoMethod.addAll(c19.elements());
    }
}
