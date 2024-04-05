package resource;

import spoon.reflect.declaration.*;

import java.util.*;
import java.util.stream.Collectors;

public class CachedElementFinder implements ElementFinder {
    private static final Map<String, CtType<?>> cachedType = new HashMap<>();
    private static final Map<String, Set<String>> cachedSubType = new HashMap<>();
    private static final Map<String, CtMethod<?>> cachedPointcutMethod = new HashMap<>();

    private static final Set<CtMethod<?>> cachedPublicMethod = new HashSet<>();

    private static CachedElementFinder cachedElementFinder;

    private CachedElementFinder() {
    }

    public static CachedElementFinder getInstance() {
        if (cachedElementFinder == null) {
            cachedElementFinder = new CachedElementFinder();
        }
        return cachedElementFinder;
    }

    public Set<CtMethod<?>> getCachedPublicMethod() {
        return cachedPublicMethod;
    }

    @Override
    public void addType(CtType<?> type) {
        cachedType.putIfAbsent(type.getQualifiedName(), type);
    }

    @Override
    public void addSuperClass(CtType<?> type, String superClass) {
        addType(type);
        var children = cachedSubType.get(superClass);
        if (children == null) {
            Set<String> c = new HashSet<>();
            c.add(type.getQualifiedName());
            cachedSubType.put(superClass, c);
        } else {
            children.add(type.getQualifiedName());
        }
    }

    @Override
    public void addPointcutMethod(CtMethod<?> method) {
        String signature = method.getDeclaringType().getQualifiedName() + "." + method.getSimpleName();
        cachedPointcutMethod.put(signature, method);
    }

    @Override
    public void addPublicMethod(CtMethod<?> method) {
        if (method.getModifiers().contains(ModifierKind.PUBLIC))
            cachedPublicMethod.add(method);
    }

    @Override
    public void addSuperInterfaces(CtType<?> type, Set<String> superInterfaces) {
        superInterfaces.forEach(i -> addSuperClass(type, i));
    }


    @Override
    public CtType<?> findType(String qualifiedName) {
        if (cachedType.containsKey(qualifiedName)) {
            return cachedType.get(qualifiedName);
        }
        var o = ModelFactory.getModel().getAllTypes()
                .stream().filter(t -> t.getQualifiedName().equals(qualifiedName))
                .findFirst();
        cachedType.put(qualifiedName, o.orElse(null));
        return o.orElse(null);
    }

    @Override
    public CtClass<?> findClass(String qualifiedName) {
        var c = findType(qualifiedName);
        if (c instanceof CtClass<?>) return (CtClass<?>) c;
        return null;
    }

    @Override
    public CtInterface<?> findInterface(String qualifiedName) {
        var c = findType(qualifiedName);
        if (c instanceof CtInterface<?>) return (CtInterface<?>) c;
        return null;
    }

    @Override
    public CtMethod<?> findPointcutMethod(String signature) {
        return cachedPointcutMethod.get(signature);
    }

    public Set<CtType<?>> directedSubType(String qualifiedName) {
        Set<CtType<?>> result = new HashSet<>();
        var type = findType(qualifiedName);
        if (type == null) return result;

        if (cachedSubType.containsKey(qualifiedName)) {
            var cs = cachedSubType.get(qualifiedName);
            result = cs.stream().map(cachedType::get).filter(Objects::nonNull).collect(Collectors.toSet());
        }
        return result;
    }


}
