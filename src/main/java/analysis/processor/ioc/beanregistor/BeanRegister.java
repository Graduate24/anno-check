package analysis.processor.ioc.beanregistor;

import analysis.processor.ioc.beanloader.BeanDefinitionModel;
import resource.CachedElementFinder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by: zhang ran
 * 2024-03-15
 */
public class BeanRegister {

    public static void register(BeanDefinitionModel beanDefinitionModel) {
        if (beanDefinitionModel != null) {
            IoCContainerModel.INSTANCE.addToNameToBeanMap(beanDefinitionModel.getName(), beanDefinitionModel);
            IoCContainerModel.INSTANCE.addToTypeToBeanMap(beanDefinitionModel.getType(), beanDefinitionModel);
        }
    }

    private static boolean isEmpty(Collection<?> e) {
        return e == null || e.isEmpty();
    }

    /**
     * get bean definition by type
     *
     * @param type type qualified name
     * @return Bean Definition set
     */
    public static Set<BeanDefinitionModel> getBeanByTypeAndSunType(String type) {
        Set<BeanDefinitionModel> result = new HashSet<>();

        CachedElementFinder f = CachedElementFinder.getInstance();
        var b = getBeanByType(type);
        if (!isEmpty(b)) {
            result.addAll(b);
        }
        var children = f.directedSubType(type);
        children.forEach(c -> {
            result.addAll(getBeanByTypeAndSunType(c.getQualifiedName()));
        });
        return result;
    }


    public static Set<BeanDefinitionModel> getBeanByType(String type) {
        return IoCContainerModel.INSTANCE.getBeanFromType(type);
    }

    /**
     * get bean definition by name
     *
     * @param name name
     * @return Bean Definition set
     */
    public static Set<BeanDefinitionModel> getBeanByName(String name) {
        return IoCContainerModel.INSTANCE.getBeanFromName(name);
    }
}
