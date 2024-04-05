package resource;

import spoon.reflect.declaration.CtMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PointcutPredictorCache {

    private static final Map<CtMethod<?>, Predicate<?>> methodResolverMap = new HashMap<>();

    private static final Map<String, Predicate<CtMethod<?>>> pointcutStringResolverMap = new HashMap<>();

    private static PointcutPredictorCache pointcutResolverCache;

    private PointcutPredictorCache() {
    }

    public static PointcutPredictorCache getInstance() {
        if (pointcutResolverCache == null) {
            pointcutResolverCache = new PointcutPredictorCache();
        }
        return pointcutResolverCache;
    }

    public boolean contains(CtMethod<?> method) {
        return methodResolverMap.containsKey(method);
    }

    public boolean contains(String pointcut) {
        return pointcutStringResolverMap.containsKey(pointcut);
    }

    public void addPredictor(CtMethod<?> method, Predicate<CtMethod<?>> resolver) {
        methodResolverMap.put(method, resolver);
    }

    public Predicate<?> getPredictor(CtMethod<?> method) {
        return methodResolverMap.get(method);
    }

    public Predicate<CtMethod<?>> getPredictor(String pointcut) {
        return pointcutStringResolverMap.get(pointcut);
    }

    public void addPredictor(String pointcut, Predicate<CtMethod<?>> resolver) {
        pointcutStringResolverMap.put(pointcut, resolver);
    }

}
