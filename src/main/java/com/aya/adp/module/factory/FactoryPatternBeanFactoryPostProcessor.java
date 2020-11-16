package com.aya.adp.module.factory;

import com.aya.adp.annotation.AdpFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ls9527
 */
public class FactoryPatternBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(AdpFactory.class);
        if (beanNames.length == 0) {
            return;
        }
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;

        List<FactoryDefinitionInfo> factoryDefinitionInfoList = new ArrayList<>();
        for (String beanName : beanNames) {
            AdpFactory adpFactory = beanFactory.findAnnotationOnBean(beanName, AdpFactory.class);
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            FactoryDefinition factoryDefinition = buildFactoryDefinition(adpFactory);
            FactoryDefinitionInfo factoryDefinitionInfo = buildFactoryDefinitionInfo(beanName, beanDefinition.isSingleton(), factoryDefinition);
            factoryDefinitionInfoList.add(factoryDefinitionInfo);
        }

        registerDefinition(beanDefinitionRegistry, factoryDefinitionInfoList);
    }

    private void registerDefinition(BeanDefinitionRegistry beanDefinitionRegistry, List<FactoryDefinitionInfo> factoryDefinitionInfoList) {
        BeanDefinition groupBeanDefinition = getGroupBeanDefinition();
        BeanNameGenerator generator = new DefaultBeanNameGenerator();

        String beanName = generator.generateBeanName(groupBeanDefinition, beanDefinitionRegistry);
        groupBeanDefinition.getPropertyValues().addPropertyValue("beanDefinitionInfo", factoryDefinitionInfoList);
        beanDefinitionRegistry.registerBeanDefinition(beanName, groupBeanDefinition);
    }

    protected FactoryDefinitionInfo buildFactoryDefinitionInfo(String beanName, boolean singleton, FactoryDefinition factoryDefinition) {
        FactoryDefinitionInfo factoryDefinitionInfo = new FactoryDefinitionInfo();
        factoryDefinitionInfo.setBeanName(beanName);
        factoryDefinitionInfo.setSingleton(singleton);
        factoryDefinitionInfo.setFactoryDefinition(factoryDefinition);
        return factoryDefinitionInfo;
    }


    private Map<String, FactoryDefinition> getFactoryDefinitionMap(Map<String, Map<String, FactoryDefinition>> cachedFactoryDefinitions, String group) {
        Map<String, FactoryDefinition> factoryDefinitionMap = null;
        if (cachedFactoryDefinitions.containsKey(group)) {
            factoryDefinitionMap = cachedFactoryDefinitions.get(group);
        }
        if (factoryDefinitionMap == null) {
            factoryDefinitionMap = new HashMap<>();
            cachedFactoryDefinitions.put(group, factoryDefinitionMap);
        }
        return factoryDefinitionMap;
    }

    private BeanDefinition getGroupBeanDefinition() {
        BeanDefinition groupBeanDefinition = new GenericBeanDefinition();
        groupBeanDefinition.setBeanClassName(DefaultDpFactories.class.getName());
        return groupBeanDefinition;
    }

    private FactoryDefinition buildFactoryDefinition(AdpFactory adpFactory) {
        DefaultFactoryDefinition factoryDefinition = new DefaultFactoryDefinition();
        factoryDefinition.setName(adpFactory.name());
        factoryDefinition.setGroup(adpFactory.group().value());
        return factoryDefinition;
    }

}
