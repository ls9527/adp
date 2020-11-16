package com.aya.adp.module.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ls9527
 */
public class DefaultDpFactories implements DpFactories, InitializingBean, ApplicationContextAware {
    private List<FactoryDefinitionInfo> beanDefinitionInfo;

    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    private AtomicBoolean changed = new AtomicBoolean(false);

    @Override
    public <T> T getBean(String name){
        return (T)beanMap.get(name);
    }

    public void setBeanDefinitionInfo(List<FactoryDefinitionInfo> beanDefinitionInfo) {
        this.beanDefinitionInfo = beanDefinitionInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(changed.compareAndSet(false,true)){
            for (FactoryDefinitionInfo factoryDefinitionInfo : beanDefinitionInfo) {
                String beanName = factoryDefinitionInfo.getBeanName();
                if(factoryDefinitionInfo.isSingleton()){
                    Object bean = applicationContext.getBean(beanName);
                    beanMap.put(factoryDefinitionInfo.getFactoryDefinition().getName(),bean);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
