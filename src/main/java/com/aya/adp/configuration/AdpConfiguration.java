package com.aya.adp.configuration;

import com.aya.adp.module.factory.FactoryPatternBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ls9527
 */
@Configuration
public class AdpConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public FactoryPatternBeanFactoryPostProcessor factoryPattern() {
        return new FactoryPatternBeanFactoryPostProcessor();
    }
}
