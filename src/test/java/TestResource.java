import analysis.collector.*;
import analysis.processor.resource.ResourceRole;
import analysis.processor.resource.ResourceScanner;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

import java.util.function.Predicate;

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
            System.out.println(e.getDeclaringType().getQualifiedName() + "#" + e.getSimpleName());
        });
        System.out.println("Repository interface: ");
        c6.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName() + "." + e.getSimpleName());
        });
        System.out.println("Repository method: ");
        c7.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Autowired field: ");
        c8.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "#" + e.getSimpleName());
        });
        System.out.println("Aspect class: ");
        c9.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName());
        });
        System.out.println("After Anno method: ");
        c10.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("AfterReturning Anno method: ");
        c11.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("AfterThrowing Anno method: ");
        c12.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Around Anno method: ");
        c13.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Before Anno method: ");
        c14.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Pointcut Anno method: ");
        c15.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Configuration class: ");
        c16.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName());
        });
        System.out.println("Controller class: ");
        c17.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getQualifiedName());
        });
        System.out.println("Mapping method: ");
        c18.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });
        System.out.println("Autowired method: ");
        c19.elements().forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });


        System.out.println("-----------");
        System.out.println("Point cuts:");
        // point cuts
        c15.elements().forEach(e -> {
            System.out.print("    ");
            e.getAnnotations().stream().filter(v -> v.getAnnotationType().getQualifiedName()
                            .equals("org.aspectj.lang.annotation.Pointcut"))
                    .findFirst().ifPresent(o -> {
                        var exp = o.getValue("value");
                        if (exp instanceof CtLiteral<?> literal) {
                            String value = (String) literal.getValue();
                            System.out.println("Annotation value: " + value);
                        }
                    });

        });

        Predicate<CtMethod<?>> p = (e)-> e.getAnnotations().stream().anyMatch(a -> "edu.tsinghua.demo.aop.Log".
                equals(a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName()));

        var customCollector = CustomCollector.newCollector(p, ResourceRole.METHOD);
        var ret = processor.scanOnceFor(launcher.getModel().getRootPackage(),customCollector);
        System.out.println("@Log method:");
        ret.forEach(e -> {
            System.out.print("    ");
            System.out.println(e.getDeclaringType().getQualifiedName() + "." + e.getSignature());
        });

    }


}
