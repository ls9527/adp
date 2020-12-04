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
package com.github.ls9527.adp.strategy;

import com.github.ls9527.adp.annotation.AdpStrategy;
import com.github.ls9527.adp.exception.MethodNotMatchException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ls9527
 */
public class StrategyProxy implements InvocationHandler {

    private Map<Method, List<StrategyMethodInvoker>> methodHandlerMap = new ConcurrentHashMap<>();

    public StrategyProxy(List<MethodInfo> objectList) {
        for (MethodInfo object : objectList) {
            AdpStrategy annotation = AnnotationUtils.findAnnotation(object.getMethod(), AdpStrategy.class);
            String condition = annotation.condition();
            int order = annotation.order();
            StrategyMethodInvoker defaultMethodHandler = buildDefaultMethodHandler(object, condition, order);
            cacheMethod(object, defaultMethodHandler);
        }
    }

    private void cacheMethod(MethodInfo object, StrategyMethodInvoker strategyMethodInvoker) {
        Method originMethod = object.getMethod();
        Class<?> currentClass = originMethod.getDeclaringClass();
        for (Class<?> anInterface : currentClass.getInterfaces()) {
            Method mostSpecificMethod = ClassUtils.getMostSpecificMethod(originMethod, anInterface);
            if (mostSpecificMethod != null) {
                List<StrategyMethodInvoker> strategyMethodInvokers =
                        methodHandlerMap.computeIfAbsent(mostSpecificMethod, x -> new ArrayList<>());
                strategyMethodInvokers.add(strategyMethodInvoker);
            }
        }
    }

    protected StrategyMethodInvoker buildDefaultMethodHandler(MethodInfo object, String condition, int order) {
        DefaultStrategyMethodInvoker defaultMethodHandler = new DefaultStrategyMethodInvoker();
        defaultMethodHandler.setBean(object.getBean());
        defaultMethodHandler.setMethod(object.getMethod());
        defaultMethodHandler.setOrder(order);
        defaultMethodHandler.setCondition(condition);
        return defaultMethodHandler;
    }

    private Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable e) {
                throw unwrapThrowable(e);
            }
        }
        StrategyMethodInvoker strategyMethodInvoker = findHandlerByArgs(method, args);
        return strategyMethodInvoker.invoke(args);
    }

    private StrategyMethodInvoker findHandlerByArgs(Method method, Object[] args) {
        List<StrategyMethodInvoker> strategyMethodInvokers = methodHandlerMap.get(method);
        StrategyMethodInvoker methodInvoker = null;
        if (strategyMethodInvokers != null) {
            // order the list
            for (StrategyMethodInvoker strategyMethodInvoker : strategyMethodInvokers) {
                if (strategyMethodInvoker.support(args)) {
                    methodInvoker = strategyMethodInvoker;
                    break;
                }
            }
        }
        // not match strategy, throw exception, not call
        if (methodInvoker == null) {
            throw new MethodNotMatchException("not match MethodHandler", method, args);
        }
        return methodInvoker;
    }
}
