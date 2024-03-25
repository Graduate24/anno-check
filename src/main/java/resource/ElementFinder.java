package resource;

import spoon.reflect.declaration.*;

import java.util.Set;

public interface ElementFinder {

    CtType<?> findType(String qualifiedName);

    CtClass<?> findClass(String qualifiedName);

    CtInterface<?> findInterface(String qualifiedName);

    Set<CtType<?>> directedSubType(String qualifiedName);

}
