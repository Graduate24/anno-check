package analysis.processor;

import analysis.collector.ElementCollector;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        super.visitCtClass(ctClass);
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        collect(ResourceRole.INTERFACE, ctInterface);
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
}
