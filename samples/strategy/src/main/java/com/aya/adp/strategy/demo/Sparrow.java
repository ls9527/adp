package com.aya.adp.strategy.demo;

import com.aya.adp.annotation.AdpFactory;
import com.aya.adp.annotation.AdpStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */
@AdpFactory(name = FactoryConstants.SPARROW, group = "bird")
@Service
public class Sparrow implements Bird {

    private static final Logger logger = LoggerFactory.getLogger(Sparrow.class);

    @AdpStrategy(el = "#{type=" + FactoryConstants.SPARROW + "}", order = 0, group = "bird")
    @Override
    public void sayHello(String type) {
        logger.info("Sparrow say hello");
    }

    @AdpStrategy(el = "#{true}", order = 100, group = "bird")
    @Override
    public void vipLevel(int totalAmount) {

        logger.info("the king vip");

    }
}
