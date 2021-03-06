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

import java.lang.reflect.InvocationTargetException;

/**
 * @author ls9527
 */
public interface StrategyMethodInvoker {
    /**
     * invoke target method
     *
     * @param args args
     * @return object
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException;

    /**
     * is support args for method
     *
     * @param args arguments
     * @return support method
     */
    boolean support(Object[] args);
}
