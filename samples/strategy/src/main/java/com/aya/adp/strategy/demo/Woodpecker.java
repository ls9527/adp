package com.aya.adp.strategy.demo;

import com.aya.adp.annotation.AdpStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */
@Service
public class Woodpecker implements Bird {

    private static final Logger logger = LoggerFactory.getLogger(Woodpecker.class);


    @AdpStrategy(el = "#{type=" + FactoryConstants.WOODPECKER + "}", order = 0, group = "bird")
    @Override
    public void sayHello(String type) {
        logger.info("Woodpecker say hello");
    }

    @AdpStrategy(el = "#{totalAmount < 2000}", order = 0, group = "bird")
    @Override
    public void vipLevel(int totalAmount) {
        logger.info("default vip");
    }
}
