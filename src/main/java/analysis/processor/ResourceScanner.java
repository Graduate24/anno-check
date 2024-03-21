package analysis.processor;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

public class ResourceScanner extends CtScanner {

    @Override
    public <T> void visitCtMethod(CtMethod<T> ctMethod) {
        System.out.println("    [Method: ]" + ctMethod.getSimpleName()+" "+ctMethod.getAnnotations());

        super.visitCtMethod(ctMethod);
    }

    @Override
    public <T> void visitCtConstructor(final CtConstructor<T> c){
        System.out.println("    [Constructor: ]" + c.getSimpleName()+ "("+c.getParameters()+")");
        super.visitCtConstructor(c);
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        System.out.println("[Class: ]" + ctClass.getQualifiedName());
        if(!ctClass.getAnnotations().isEmpty()){
            System.out.println("  Annotations: "+ctClass.getAnnotations());
        }
        if (ctClass.getSuperclass() != null) {
            System.out.println("  Direct Parent: " + ctClass.getSuperclass().getQualifiedName());
        }
        if(!ctClass.getSuperInterfaces().isEmpty())
            System.out.println("  Direct Interface: " + ctClass.getSuperInterfaces());
        super.visitCtClass(ctClass);
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        System.out.println("[Interface: ]" + ctInterface.getQualifiedName());
        if(!ctInterface.getAnnotations().isEmpty()){
            System.out.println("  Annotations: "+ctInterface.getAnnotations());
        }
        for (var superInterface : ctInterface.getSuperInterfaces()) {
            System.out.println("  Direct Parent: " + superInterface.getQualifiedName());
        }
        super.visitCtInterface(ctInterface);
    }
}
