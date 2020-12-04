package com.github.ls9527.adp.strategy.range.demo;


import com.github.ls9527.adp.annotation.AdpStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author ls9527
 */
@Service
public class RangeWoodpecker implements RangeBird {

    private static final Logger logger = LoggerFactory.getLogger(RangeWoodpecker.class);

    @AdpStrategy(condition = "#{age >=20}")
    @Override
    public void sayHello(int age) {
        logger.info("Woodpecker say hello");
    }
}
