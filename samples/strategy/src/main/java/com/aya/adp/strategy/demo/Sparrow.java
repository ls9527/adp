package com.aya.adp.strategy.demo;

import com.aya.adp.annotation.AdpFactory;
import com.aya.adp.annotation.AdpGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */
@AdpFactory(name = FactoryConstants.SPARROW, group = @AdpGroup("bird"))
@Service
public class Sparrow implements Bird {

    private static final Logger logger = LoggerFactory.getLogger(Sparrow.class);

    @Override
    public void sayHello(String type) {
        logger.info("Sparrow say hello");
    }
}
