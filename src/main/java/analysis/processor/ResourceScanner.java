package analysis.processor;

import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.List;

public class ResourceScanner extends CtScanner {

    private List<CtClass<?>> classList = new ArrayList<>();
    private List<CtMethod<?>> methodList = new ArrayList<>();
    private List<CtInterface<?>> interfaceList = new ArrayList<>();

    private List<ElementCollector<?>> elementCollectors;


    @Override
    public <T> void visitCtField(final CtField<T> f) {
        System.out.println("    [Field: ]" + f.getSimpleName() + "=" + f.getAssignment() + " " + f.getAnnotations());

    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> ctMethod) {
        System.out.println("    [Method: ]" + ctMethod.getSimpleName() + " " + ctMethod.getAnnotations());

        super.visitCtMethod(ctMethod);
    }

    @Override
    public <T> void visitCtConstructor(final CtConstructor<T> c) {
        System.out.println("    [Constructor: ]" + c.getBody().getStatements() + " " + c.getSimpleName() + "(" + c.getParameters() + ")");
        super.visitCtConstructor(c);
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        System.out.println("[Class: ]" + ctClass.getQualifiedName());
        if (!ctClass.getAnnotations().isEmpty()) {
            System.out.println("  Annotations: " + ctClass.getAnnotations());
        }
        if (ctClass.getSuperclass() != null) {
            System.out.println("  Direct Parent: " + ctClass.getSuperclass().getQualifiedName());
        }
        if (!ctClass.getSuperInterfaces().isEmpty())
            System.out.println("  Direct Interface: " + ctClass.getSuperInterfaces());
        super.visitCtClass(ctClass);
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        System.out.println("[Interface: ]" + ctInterface.getQualifiedName());
        if (!ctInterface.getAnnotations().isEmpty()) {
            System.out.println("  Annotations: " + ctInterface.getAnnotations());
        }
        for (var superInterface : ctInterface.getSuperInterfaces()) {
            System.out.println("  Direct Parent: " + superInterface.getQualifiedName());
        }
        super.visitCtInterface(ctInterface);
    }

    public List<CtClass<?>> getClassList() {
        return classList;
    }

    public void setClassList(List<CtClass<?>> classList) {
        this.classList = classList;
    }

    public List<CtMethod<?>> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<CtMethod<?>> methodList) {
        this.methodList = methodList;
    }

    public List<CtInterface<?>> getInterfaceList() {
        return interfaceList;
    }

    public void setInterfaceList(List<CtInterface<?>> interfaceList) {
        this.interfaceList = interfaceList;
    }
}
