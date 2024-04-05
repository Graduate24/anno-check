package analysis.processor.aop.resolver.builder;

import java.util.Set;

/**
 * Created by: zhang ran
 * 2024-04-05
 */
public interface PointcutResolverBuilder<T, R> {
    R build(Set<T> contextResource, T currentResource);
}
