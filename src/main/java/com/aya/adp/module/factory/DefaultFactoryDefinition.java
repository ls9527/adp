package com.aya.adp.module.factory;

/**
 * @author ls9527
 */
public class DefaultFactoryDefinition implements FactoryDefinition{
    private String name;
    private String group;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
