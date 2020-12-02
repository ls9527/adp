/*
 * Copyright © 2020 ls9527 (364173778@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ls9527.adp.adp.module.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ls9527
 */
public class DefaultFactory implements Factory<Map<String, Object>>, InitializingBean, BeanFactoryAware {
    private static final Logger logger = LoggerFactory.getLogger(DefaultFactory.class);
    private final Map<String, Map<String, Object>> beanMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Object>> classMap = new ConcurrentHashMap<>();
    private final AtomicBoolean changed = new AtomicBoolean(false);
    private List<FactoryDefinitionInfo> beanDefinitionInfo;
    private BeanFactory beanFactory;

    @Override
    public Map<String, Object> getBean(String name) {
        return beanMap.get(name);
    }

    public Map<String, Object> getGroupBeanByClass(Class<?> interfaceType) {
        return classMap.get(interfaceType);
    }

    public void setBeanDefinitionInfo(List<FactoryDefinitionInfo> beanDefinitionInfo) {
        this.beanDefinitionInfo = beanDefinitionInfo;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (changed.compareAndSet(false, true)) {
            if (beanDefinitionInfo == null) {
                logger.info("beanDefinitionInfo is null");
                return;
            }
            for (FactoryDefinitionInfo factoryDefinitionInfo : beanDefinitionInfo) {
                String beanName = factoryDefinitionInfo.getBeanName();
                if (factoryDefinitionInfo.isSingleton()) {
                    Object bean = beanFactory.getBean(beanName);
                    beanMap.computeIfAbsent(factoryDefinitionInfo.getGroup(), k -> new ConcurrentHashMap<>(16))
                            .put(factoryDefinitionInfo.getType(), bean);
                }
            }

            for (FactoryDefinitionInfo factoryDefinitionInfo : beanDefinitionInfo) {
                String beanName = factoryDefinitionInfo.getBeanName();
                if (factoryDefinitionInfo.isSingleton()) {
                    Object bean = beanFactory.getBean(beanName);
                    Class<?> currentClass = bean.getClass();
                    while (!currentClass.equals(Object.class)) {
                        Class<?>[] interfaces = currentClass.getInterfaces();
                        if (interfaces.length > 0) {
                            for (Class<?> anInterface : interfaces) {
                                // ignore serializable
                                if (anInterface.getMethods().length > 0) {
                                    classMap.computeIfAbsent(anInterface, k -> new ConcurrentHashMap<>(16))
                                            .put(factoryDefinitionInfo.getType(), bean);
                                }
                            }
                        }
                        currentClass = currentClass.getSuperclass();
                    }

                }
            }

        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
