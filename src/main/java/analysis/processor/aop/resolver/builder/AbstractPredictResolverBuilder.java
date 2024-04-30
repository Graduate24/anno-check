package analysis.processor.aop.resolver.builder;

import analysis.processor.aop.parser.Expr;
import analysis.processor.aop.parser.Parser;
import analysis.processor.aop.parser.Scanner;
import analysis.processor.aop.parser.Token;
import analysis.processor.aop.resolver.PredictorResolver;
import resource.PointcutPredictorCache;
import spoon.reflect.declaration.CtMethod;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static resource.ElementUtil.getValueOfAnnotationAsString;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public abstract class AbstractPredictResolverBuilder<E> implements PointcutResolverBuilder<CtMethod<?>, Predicate<E>> {
    private final PointcutPredictorCache pointcutResolverCache = PointcutPredictorCache.getInstance();

    protected abstract String targetAnnotation();

    public abstract String name();

    @Override
    @SuppressWarnings("unchecked")
    public Predicate<E> build(Set<CtMethod<?>> contextResource, CtMethod<?> currentResource) {
        if (pointcutResolverCache.contains(currentResource)) {
            System.out.println("hit cache");
            return (Predicate<E>) pointcutResolverCache.getPredictor(currentResource);
        }
        var pack = currentResource.getDeclaringType().getPackage().toString();
        boolean wildcardImport = currentResource.getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().getPackage().toString().equals(pack));
        var targetAnnotation = targetAnnotation();
        if (wildcardImport) {
            targetAnnotation = pack + "." +
                    targetAnnotation.substring(targetAnnotation.lastIndexOf(".") + 1);
        }
        String value = getValueOfAnnotationAsString(currentResource, targetAnnotation);
        System.out.println(currentResource.getType().getQualifiedName() + " " + value);
        if (value == null) return null;
        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanTokens();
        if (scanner.isHasError()) {
            return null;
        }
        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();
        if (parser.isHasError()) {
            return null;
        }
        PredictorResolver<CtMethod<?>> resolver = new PredictorResolver<>(expr,
                currentResource.getDeclaringType().getPackage().toString(),
                currentResource.getDeclaringType().getSimpleName());
        resolver.setSource(value);
        var predictor = resolver.resolvePredictor();
        if (predictor == null) return null;
        pointcutResolverCache.addPredictor(currentResource, predictor);
        pointcutResolverCache.addPredictor(value, predictor);
        System.out.println("predictor for :" + value);
        return (Predicate<E>) predictor;
    }
}
