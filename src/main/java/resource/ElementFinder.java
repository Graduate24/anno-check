package resource;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.Set;

/**
 * Created by: zhang ran
 * 2024-03-16
 */
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
