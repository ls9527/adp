package com.github.ls9527.adp.strategy.range.demo;


import com.github.ls9527.adp.annotation.AdpStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@Service
public class RangeSparrow implements RangeBird {

    private static final Logger logger = LoggerFactory.getLogger(RangeSparrow.class);

    @AdpStrategy(condition = "#{age > 0 and age < 20}",order = 0)
    @Override
    public void sayHello(int age) {
        logger.info("Sparrow say hello");
    }
}
