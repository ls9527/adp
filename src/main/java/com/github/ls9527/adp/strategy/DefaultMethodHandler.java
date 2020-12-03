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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ls9527
 */
public class DefaultMethodHandler implements MethodHandler, Ordered {
    private Object bean;
    private Method method;
    private int order;
    private StandardEvaluationContext evaluationContext;
    private Expression expression;

    @Override
    public Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(bean, args);
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultMethodHandler.class);

    @Override
    public boolean match(Object[] args) {
        Map<String, Object> rootMap = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameterType = parameters[i];
            Object arg = args[i];
            rootMap.put(parameterType.getName(), arg);
        }
        Object value = null;
        try {
            value = expression.getValue(evaluationContext, rootMap);
        } catch (Throwable e) {
            logger.error("getValue error,method: {}", method.getName(), e);
            return false;
        }
        if (value == null) {
            return false;
        }
        if (!(value instanceof Boolean)) {
            throw new RuntimeException("return type can not cast to boolean");
        }
        return (Boolean) value;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setEvaluationContext(StandardEvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
