package resource;

import analysis.collector.ElementCollector;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceScanner extends CtScanner {

    private List<ElementCollector<? extends CtElement>> collectors;
    private Map<ResourceRole, List<ElementCollector<? extends CtElement>>> collectorMap;

    @Override
    public <T> void visitCtField(final CtField<T> f) {
        collect(ResourceRole.FIELD, f);
        super.visitCtField(f);
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> ctMethod) {
        collect(ResourceRole.METHOD, ctMethod);
        super.visitCtMethod(ctMethod);
    }

    @Override
    public <T> void visitCtConstructor(final CtConstructor<T> c) {
        collect(ResourceRole.CONSTRUCTOR, c);
        super.visitCtConstructor(c);
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        collect(ResourceRole.CLASS, ctClass);
        CachedElementFinder.getInstance().addType(ctClass);
        if (ctClass.getSuperclass() != null) {
            CachedElementFinder.getInstance().addSuperClass(ctClass,  ctClass.getSuperclass().getQualifiedName());
        }
        if (!ctClass.getSuperInterfaces().isEmpty()) {
            ctClass.getSuperInterfaces().forEach(i -> {
                CachedElementFinder.getInstance().addSuperClass(ctClass,  i.getQualifiedName());
            });
        }
        super.visitCtClass(ctClass);
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        collect(ResourceRole.INTERFACE, ctInterface);
        CachedElementFinder.getInstance().addType(ctInterface);
        if (!ctInterface.getSuperInterfaces().isEmpty()) {
            ctInterface.getSuperInterfaces().forEach(i -> CachedElementFinder.getInstance().addSuperClass(ctInterface, i.getQualifiedName()));
        }
        super.visitCtInterface(ctInterface);
    }

    public void addCollector(ElementCollector<? extends CtElement> collector) {
        if (this.collectors == null) {
            this.collectors = new ArrayList<>();
        }
        this.collectors.add(collector);
        this.collectorMap = this.collectors.stream().collect(Collectors.groupingBy(ElementCollector::role));
    }

    private <T extends CtElement> void collect(ResourceRole role, T e) {
        if (collectorMap != null) {
            var c = collectorMap.get(role);
            if (c != null) {
                c.forEach(co -> co.collect(e));
            }
        }
    }

    public <E extends CtElement> Collection<E> scanOnceFor(CtPackage ctPackage, ElementCollector<E> collector) {
        var oldCollectorMap = collectorMap;
        var oldCollectors = collectors;
        collectors = Collections.singletonList(collector);
        collectorMap = this.collectors.stream().collect(Collectors.groupingBy(ElementCollector::role));
        this.scan(ctPackage);
        var result = collector.elements();
        collectors = oldCollectors;
        collectorMap = oldCollectorMap;
        return result;
    }
}
