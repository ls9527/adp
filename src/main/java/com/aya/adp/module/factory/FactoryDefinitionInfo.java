package com.aya.adp.module.factory;

/**
 * @author ls9527
 */
public class FactoryDefinitionInfo {
    private String beanName;
    private boolean isSingleton = true;
    private FactoryDefinition factoryDefinition;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public FactoryDefinition getFactoryDefinition() {
        return factoryDefinition;
    }

    public void setFactoryDefinition(FactoryDefinition factoryDefinition) {
        this.factoryDefinition = factoryDefinition;
    }
}
