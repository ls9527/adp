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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ls9527
 */
public class StrategyProxy implements InvocationHandler {


    public static final ParserContext CONTEXT = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "#{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    };


    public StrategyProxy(List<MethodInfo> objectList) {
        for (MethodInfo object : objectList) {
            AdpStrategy annotation = AnnotationUtils.findAnnotation(object.getMethod(), AdpStrategy.class);
            String condition = annotation.condition();
            int order = annotation.order();
            DefaultMethodHandler defaultMethodHandler = buildDefaultMethodHandler(object, condition, order);
            cacheMethod(object, defaultMethodHandler);
        }
    }

    private void cacheMethod(MethodInfo object, DefaultMethodHandler defaultMethodHandler) {
        Method originMethod = object.getMethod();
        Class<?> currentClass = originMethod.getDeclaringClass();
        for (Class<?> anInterface : currentClass.getInterfaces()) {
            Method mostSpecificMethod = ClassUtils.getMostSpecificMethod(originMethod, anInterface);
            if(mostSpecificMethod!=null){
                List<MethodHandler> methodHandlers =
                        methodHandlerMap.computeIfAbsent(mostSpecificMethod, x -> new ArrayList<>());
                methodHandlers.add(defaultMethodHandler);
            }
        }
    }

    SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    StandardEvaluationContext evaluationContext = new StandardEvaluationContext(){
        {
            addPropertyAccessor(new MapAccessor());
            addPropertyAccessor(new BeanExpressionContextAccessor());
            addPropertyAccessor(new ReflectivePropertyAccessor());
        }
    };

    private DefaultMethodHandler buildDefaultMethodHandler(MethodInfo object, String condition, int order) {
        DefaultMethodHandler defaultMethodHandler = new DefaultMethodHandler();
        defaultMethodHandler.setBean(object.getBean());
        defaultMethodHandler.setMethod(object.getMethod());
        defaultMethodHandler.setOrder(order);

        Expression expression = spelExpressionParser.parseExpression(condition, CONTEXT);

        defaultMethodHandler.setExpression(expression);
        defaultMethodHandler.setEvaluationContext(evaluationContext);
        return defaultMethodHandler;
    }


    Map<Method, List<MethodHandler>> methodHandlerMap = new ConcurrentHashMap<>();


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHandler methodHandler = findHandlerByArgs(method, args);
        // not match strategy, throw exception, not call
        if (methodHandler == null) {
            throw new RuntimeException("not match MethodHandler");
        }
        return methodHandler.invoke(args);
    }

    private MethodHandler findHandlerByArgs(Method method, Object[] args) {
        List<MethodHandler> methodHandlers = methodHandlerMap.get(method);
        if (methodHandlers == null) {
            throw new RuntimeException("not found MethodHandlers");
        }
        // order the list
        for (MethodHandler methodHandler : methodHandlers) {
            if (methodHandler.match(args)) {
                return methodHandler;
            }
        }
        return null;
    }
}
