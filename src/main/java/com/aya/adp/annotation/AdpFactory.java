package com.aya.adp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ls9527
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdpFactory {

    /**
     * factory name
     *
     * @return
     */
    String name();

    /**
     * group info
     * @return
     */
    AdpGroup group();
}
