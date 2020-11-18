package com.aya.adp.module.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ls9527
 */
public class GroupDpFactories implements DpFactories {
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public <T> T getBean(String name) {
        return (T) beanMap.get(name);
    }

    public void setBeanMap(Map<String, Object> beanMap) {
        this.beanMap = beanMap;
    }
}
