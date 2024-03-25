import resource.ResourceScanner;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.lang.reflect.Method;

public class Test1 {
    @Test
    public void test1() {
        CtClass<?> l = Launcher.parseClass("class A { @Service()void m() { System.out.println(\"yeah\");} }");
        System.out.println(l.toString());
        l.getMethods().forEach(System.out::print);
    }

    @Test
    public void test2(){
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        System.out.println("interfaces: ");
        launcher.getModel().getElements(new TypeFilter<>(CtInterface.class)).forEach(c->{
            System.out.println(c.getSimpleName()+", "+c.getQualifiedName()+", "+c.getSuperInterfaces());
        });
        System.out.println("classes: ");
        launcher.getModel().getElements(new TypeFilter<>(CtClass.class)).forEach(c->{
            System.out.println(c.getSimpleName()+", "+c.getSuperclass()+", "+ c.getSuperInterfaces());
        });
    }

    @Test
    public void test3(){
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        launcher.process();
        ResourceScanner processor = new ResourceScanner();
        processor.scan(launcher.getModel().getRootPackage());
    }
    public Method getReflectionMethod(CtMethod<?> ctMethod) {
        try {
            // Get the declaring class of the CtMethod
            Class<?> clazz = Class.forName(ctMethod.getDeclaringType().getQualifiedName());

            // Get parameter types for the method
            Class<?>[] parameterTypes = ctMethod.getParameters().stream()
                    .map(param -> convertToClass(param.getType()))
                    .toArray(Class<?>[]::new);

            // Use reflection to find the matching method
            return clazz.getDeclaredMethod(ctMethod.getSimpleName(), parameterTypes);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> convertToClass(CtTypeReference<?> typeReference) {
        try {
            return Class.forName(typeReference.getQualifiedName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
