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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ls9527
 */
public class GroupDpFactories<T> implements DpFactories<T> {
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public T getGroupBean(String name) {
        return (T) beanMap.get(name);
    }


    public void setBeanMap(Map<String, Object> beanMap) {
        this.beanMap = beanMap;
    }
}
