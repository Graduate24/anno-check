package analysis.processor.ioc.beanregistor;

import analysis.processor.ioc.beanloader.BeanDefinitionModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public enum IoCContainerModel {
    INSTANCE;

    private final Map<String, Set<BeanDefinitionModel>> nameToBeanMap;
    private final Map<String, Set<BeanDefinitionModel>> typeToBeanMap;

    IoCContainerModel() {
        nameToBeanMap = new HashMap<>();
        typeToBeanMap = new HashMap<>();
    }

    public void reset() {
        nameToBeanMap.clear();
        typeToBeanMap.clear();
    }

    public void addToNameToBeanMap(String name, BeanDefinitionModel beanDefinitionModel) {
        var beans = nameToBeanMap.computeIfAbsent(name, k -> new HashSet<>());
        beans.add(beanDefinitionModel);
    }

    public void addToTypeToBeanMap(String typeQualifier, BeanDefinitionModel beanDefinitionModel) {
        var beans = typeToBeanMap.computeIfAbsent(typeQualifier, k -> new HashSet<>());
        beans.add(beanDefinitionModel);
    }

    public Set<BeanDefinitionModel> getBeanFromName(String name) {
        return nameToBeanMap.get(name);
    }

    public Set<BeanDefinitionModel> getBeanFromType(String name) {
        return typeToBeanMap.get(name);
    }


    private void printMap(Map<String, Set<BeanDefinitionModel>> m) {
        m.forEach((k, v) -> {
            System.out.println(k + " : ");
            v.forEach(b -> {
                System.out.println("   [" + b.getName() + "," + b.getType() + "]");
            });
            System.out.println();
        });
    }

    public void debug() {
        System.out.println("---nameToBeanMap---");
        printMap(nameToBeanMap);
        System.out.println();
        System.out.println("---typeToBeanMap---");
        printMap(typeToBeanMap);
    }

}
