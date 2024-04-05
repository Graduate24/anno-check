package resource;

import spoon.reflect.declaration.*;

import java.util.Set;

public interface ElementFinder {

    void addType(CtType<?> type);

    void addSuperClass(CtType<?> type, String superClass);

    void addPointcutMethod(CtMethod<?> method);

    void addPublicMethod(CtMethod<?> method);

    void addSuperInterfaces(CtType<?> type, Set<String> superInterfaces);

    CtType<?> findType(String qualifiedName);

    CtClass<?> findClass(String qualifiedName);

    CtInterface<?> findInterface(String qualifiedName);

    CtMethod<?> findPointcutMethod(String signature);

    Set<CtType<?>> directedSubType(String qualifiedName);

}
