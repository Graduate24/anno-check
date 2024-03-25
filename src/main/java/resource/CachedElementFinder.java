package resource;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CachedElementFinder implements ElementFinder {
    private static final Map<String, CtType<?>> cachedType = new HashMap<>();
    private static final Map<String, Set<CtType<?>>> cachedSubType = new HashMap<>();
    private static CachedElementFinder cachedElementFinder;

    public static CachedElementFinder getInstance() {
        if (cachedElementFinder == null) {
            cachedElementFinder = new CachedElementFinder();
        }
        return cachedElementFinder;
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

    public Set<CtType<?>> directedSubType(String qualifiedName) {
        Set<CtType<?>> result = new HashSet<>();
        var type = findType(qualifiedName);
        if (type == null) return result;

        if (cachedSubType.containsKey(qualifiedName)) {
            return cachedSubType.get(qualifiedName);
        }

        result = ModelFactory.getModel().getAllTypes().stream()
                .filter(t -> (t.getSuperclass() != null && t.getSuperclass().getTypeDeclaration().getQualifiedName().equals(qualifiedName)
                        || (t.getSuperInterfaces().stream().anyMatch(i -> i.getQualifiedName().equals(qualifiedName))))
                ).collect(Collectors.toSet());
        cachedSubType.put(qualifiedName, result);
        return result;
    }


}
