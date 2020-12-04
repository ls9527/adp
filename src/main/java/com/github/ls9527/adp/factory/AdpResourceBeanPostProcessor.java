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
package com.github.ls9527.adp.factory;

import com.github.ls9527.adp.annotation.AdpFactory;
import com.github.ls9527.adp.annotation.AdpStrategy;
import com.github.ls9527.adp.annotation.FactoryResource;
import com.github.ls9527.adp.annotation.StrategyResource;
import com.github.ls9527.adp.context.Factory;
import com.github.ls9527.adp.strategy.MethodInfo;
import com.github.ls9527.adp.strategy.StrategyProxy;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ls9527
 */
public class AdpResourceBeanPostProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware,
        BeanClassLoaderAware {

    /**
     * cache for inject factory
     */
    private final Map<CacheKey, Object> factoryMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> strategyMap = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;
    private ClassLoader beanClassLoader;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = buildResourceMetadata(bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
        }
        return pvs;
    }

    private InjectionMetadata buildResourceMetadata(final Class<?> clazz) {
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (field.isAnnotationPresent(FactoryResource.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@FactoryResource annotation is not supported on static fields");
                    }
                    currElements.add(new FactoryResourceElement(field));
                }
            });

            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (field.isAnnotationPresent(StrategyResource.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@StrategyResource annotation is not supported on static fields");
                    }
                    currElements.add(new StrategyResourceElement(field));
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return new InjectionMetadata(clazz, elements);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /**
     * except singleton bean scope
     *
     * @param interfaceType      bean type
     * @param requestingBeanName bean name
     * @return lazy bean
     */
    protected Object buildLazyResourceProxy(Class<?> interfaceType, final String requestingBeanName) {
        TargetSource ts = new TargetSource() {
            @Override
            public Class<?> getTargetClass() {
                return interfaceType;
            }

            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public Object getTarget() {
                return applicationContext.getBean(requestingBeanName, interfaceType);
            }

            @Override
            public void releaseTarget(Object target) {
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        if (interfaceType.isInterface()) {
            pf.addInterface(interfaceType);
        }

        return pf.getProxy(this.beanClassLoader);
    }

    /**
     * factory cache key
     */
    private static class CacheKey {
        private String groupName;
        private Class<?> interfaceType;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CacheKey cacheKey = (CacheKey) o;

            if (this.groupName == null) {
                // ignore null value for groupName
            } else if (!groupName.equals(cacheKey.groupName)) {
                return false;
            }
            return interfaceType.equals(cacheKey.interfaceType);
        }

        @Override
        public int hashCode() {
            int result = groupName != null ? groupName.hashCode() : 0;
            result = 31 * result + (interfaceType != null ? interfaceType.hashCode() : 0);
            return result;
        }
    }

    private class FactoryResourceElement extends InjectionMetadata.InjectedElement {
        private final Class<?> interfaceType;
        private String groupName;

        public FactoryResourceElement(Field field) {
            super(field, null);
            FactoryResource factoryResource = field.getAnnotation(FactoryResource.class);
            if (!StringUtils.isEmpty(factoryResource.group())) {
                this.groupName = factoryResource.group();
            }

            Type type = field.getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new InvalidPropertyException(field.getClass(),
                        field.getName(),
                        "property is not a ParameterizedType, memberName:" + field.getName());
            }
            ParameterizedType genericType = (ParameterizedType) type;
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (actualTypeArguments.length != 1) {
                throw new InvalidPropertyException(field.getClass(),
                        field.getName(),
                        "property is not a type of class, memberName: " + field.getName());
            }
            interfaceType = (Class<?>) actualTypeArguments[0];
            if (!interfaceType.isInterface()) {
                throw new BeanCreationException("the bean type is not interface, class type : "
                        + interfaceType);
            }
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            CacheKey cacheKey = buildCache(this.interfaceType, this.groupName);
            Object factory = factoryMap.get(cacheKey);
            if (factory == null) {
                Map<String, Object> beanMap = determineObjectMap(applicationContext, this.groupName, this.interfaceType);
                factory = GroupFactory.createFactory(beanMap);
                factoryMap.put(cacheKey, factory);
            }
            return factory;
        }

        private CacheKey buildCache(Class<?> interfaceType, String groupName) {
            CacheKey cacheKey = new CacheKey();
            cacheKey.groupName = groupName;
            cacheKey.interfaceType = interfaceType;
            return cacheKey;
        }


        private Map<String, Object> determineObjectMap(ApplicationContext applicationContext,
                                                       String groupName,
                                                       Class<?> interfaceType) {

            String[] beanNamesForType = applicationContext.getBeanNamesForType(interfaceType);
            if (beanNamesForType.length == 0) {
                throw new InvalidPropertyException(this.member.getClass(),
                        this.member.getName(),
                        "the bean type has not implementation it , interfaceType: " + interfaceType);
            }
            Map<String, Object> objectMap = new HashMap<>(16);
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
            for (String beanName : beanNamesForType) {
                BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);

                Class<?> beanClass = applicationContext.getType(beanName);
                AdpFactory factory = AnnotationUtils.findAnnotation(beanClass, AdpFactory.class);
                if (factory == null) {
                    continue;
                }
                String group = factory.group();
                Object bean = null;
                if (beanDefinition.isSingleton() && !beanDefinition.isLazyInit()) {
                    bean = applicationContext.getBean(beanName);
                } else {
                    // prototype, request ,session or other scope
                    bean = buildLazyResourceProxy(interfaceType, beanName);
                }
                Boolean isMatch = matchGroup(groupName, group);
                if (isMatch) {
                    for (String name : factory.name()) {
                        objectMap.put(name, bean);
                    }
                }
            }
            return objectMap;
        }

        private Boolean matchGroup(String groupName, String group) {
            Boolean isMatch = null;
            if (groupName == null) {
                isMatch = true;
            }
            if (isMatch == null && !StringUtils.isEmpty(groupName) && groupName.equals(group)) {
                isMatch = true;
            }
            if (isMatch == null) {
                isMatch = false;
            }
            return isMatch;
        }

    }

    private class StrategyResourceElement extends InjectionMetadata.InjectedElement {
        List<MethodInfo> methodInfos = new ArrayList<>();
        Class<?> fieldType = null;

        public StrategyResourceElement(Field field) {
            super(field, null);

            fieldType = field.getType();
            if (!fieldType.isInterface()) {
                throw new IllegalArgumentException("field must be a interface, fieldName:" + field.getName());
            }
            String[] beanNamesForType = applicationContext.getBeanNamesForType(fieldType);
            if (beanNamesForType == null || beanNamesForType.length == 0) {
                throw new IllegalArgumentException("the spring container must have least a instance , type:" + fieldType.getName());
            }
            for (String beanName : beanNamesForType) {
                Class<?> beanType = applicationContext.getType(beanName);

                BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;

                BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);
                ReflectionUtils.doWithMethods(beanType, method -> {
                    if (method.isAnnotationPresent(AdpStrategy.class)) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@AdpStrategy annotation is not supported on static fields");
                        }
                        AdpStrategy annotation = method.getAnnotation(AdpStrategy.class);
                        if (annotation != null) {
                            Object bean = null;
                            if (beanDefinition.isSingleton() && !beanDefinition.isLazyInit()) {
                                bean = applicationContext.getBean(beanName);
                            } else {
                                bean = buildLazyResourceProxy(beanType, beanName);
                            }
                            MethodInfo methodInfo = new MethodInfo();
                            methodInfo.setBean(bean);
                            methodInfo.setBeanType(beanType);
                            methodInfo.setMethod(method);
                            methodInfo.setCondition(annotation.condition());
                            methodInfo.setOrder(annotation.order());
                            methodInfos.add(methodInfo);
                        }
                    }
                });
                methodInfos.sort(Comparator.comparingInt(MethodInfo::getOrder));
            }
        }


        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            Object bean = strategyMap.get(fieldType);
            if (bean == null) {
                bean = Proxy.newProxyInstance(beanClassLoader,
                        new Class[]{fieldType},
                        new StrategyProxy(methodInfos));
                strategyMap.put(fieldType, bean);
            }
            return bean;
        }


    }
}
