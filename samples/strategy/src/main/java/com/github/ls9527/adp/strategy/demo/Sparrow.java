package com.github.ls9527.adp.strategy.demo;


import com.github.ls9527.adp.annotation.AdpStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@Service
public class Sparrow implements Bird {

    private static final Logger logger = LoggerFactory.getLogger(Sparrow.class);

    @AdpStrategy(condition = "#{T(com.github.ls9527.adp.strategy.demo.FactoryConstants).SPARROW eq type}")
    @Override
    public void sayHello(String type, int age) {
        logger.info("Sparrow say hello");
    }
}
