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
package com.aya.adp.module.factory;

import com.aya.adp.annotation.AdpFactory;
import com.aya.adp.annotation.AdpResource;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ls9527
 */
public class FactoryResourceBeanPostProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware,
        BeanClassLoaderAware {

    private ApplicationContext applicationContext;
    private ClassLoader classLoader;


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
                if (field.isAnnotationPresent(AdpResource.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@AdpResource annotation is not supported on static fields");
                    }
                    currElements.add(new AdpResourceElement(field));
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

        this.classLoader = classLoader;
    }


    private class AdpResourceElement extends InjectionMetadata.InjectedElement {
        private final Class<?> interfaceType;
        private String groupName;

        public AdpResourceElement(Field field) {
            super(field, null);
            AdpResource adpResource = field.getAnnotation(AdpResource.class);
            if (!StringUtils.isEmpty(adpResource.group())) {
                this.groupName = adpResource.group();
            }

            Type type = field.getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new InvalidPropertyException(this.member.getClass(),
                        this.member.getName(),
                        "property is not a ParameterizedType, memberName:" + this.member.getName());
            }
            ParameterizedType genericType = (ParameterizedType) type;
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (actualTypeArguments.length != 1) {
                throw new InvalidPropertyException(this.member.getClass(),
                        this.member.getName(),
                        "property is not a type of class, memberName: " + this.member.getName());
            }
            interfaceType = (Class<?>) actualTypeArguments[0];
            if (!interfaceType.isInterface()) {
                throw new BeanCreationException("the bean type is not interface, class type : "
                        + interfaceType);
            }
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {

            DefaultFactory factory = applicationContext.getBean(DefaultFactory.class.getName(), DefaultFactory.class);

            Map<String, Object> beanMap = determineObjectMap(applicationContext, factory, this.groupName, this.interfaceType);

            return GroupFactory.createFactory(beanMap);
        }


        private Map<String, Object> determineObjectMap(ApplicationContext applicationContext,
                                                       DefaultFactory dpFactories,
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
                if (beanDefinition.isSingleton()) {
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

        return pf.getProxy(this.classLoader);
    }

}
