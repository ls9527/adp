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

import com.aya.adp.annotation.AdpResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ls9527
 */
public class FactoryResourceBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

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
                    currElements.add(new AdpResourceElement(field, field, null));
                }
            });

            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return new InjectionMetadata(clazz, elements);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private class AdpResourceElement extends InjectionMetadata.InjectedElement {
        private String groupName;
        private Class<?> interfaceType;

        public AdpResourceElement(Field field, AnnotatedElement annotatedElement, PropertyDescriptor pd) {
            super(field, pd);
            AdpResource adpResource = annotatedElement.getAnnotation(AdpResource.class);
            if (!StringUtils.isEmpty(adpResource.group())) {
                this.groupName = adpResource.group();
            }
            ParameterizedType genericType = (ParameterizedType) ((Field) member).getGenericType();
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (actualTypeArguments.length != 1) {
                throw new InvalidPropertyException(this.member.getClass(), this.member.getName(), "property is not a type of class: " + this.member.getName());
            }
            interfaceType = (Class<?>) actualTypeArguments[0];
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {

            DefaultDpFactories<Map<String, Object>> dpFactories = beanFactory.getBean(DefaultDpFactories.class);

            Map<String, Object> beanMap = getObjectMap(dpFactories);

            checkInjectType(beanMap.values());

            GroupDpFactories<Map<String, Object>> groupDpFactories = new GroupDpFactories<>();
            groupDpFactories.setBeanMap(beanMap);
            return groupDpFactories;
        }

        private void checkInjectType(Collection<Object> values) {
            for (Object object : values) {
                Class<?> beanClass = object.getClass();
                if (!interfaceType.isAssignableFrom(beanClass)) {
                    throw new InvalidPropertyException(beanClass, this.member.getName(), "property is not class:" + beanClass.getName());
                }
            }
        }

        private Map<String, Object> getObjectMap(DefaultDpFactories<Map<String, Object>> dpFactories) {
            Map<String, Object> beanMap = null;
            if (this.groupName != null) {
                beanMap = dpFactories.getGroupBean(this.groupName);
            }
            if (beanMap == null) {
                beanMap = dpFactories.getGroupBeanByClass(interfaceType);
                if (beanMap == null) {
                    throw new BeanCreationException("the bean type has not implementation object , interfaceType: " + this.interfaceType);
                }
            }
            return beanMap;
        }

    }

}
