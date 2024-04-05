package analysis.processor.aop.resolver.builder;

import spoon.reflect.declaration.CtElement;

import java.util.Set;

public interface PointcutResolverBuilder<T, R> {
    R build(Set<T> contextResource, T currentResource);
}
