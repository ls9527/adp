package com.github.ls9527.adp.factory.demo;


import com.github.ls9527.adp.adp.annotation.AdpFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@AdpFactory(name = FactoryConstants.SPARROW)
@Service
public class Sparrow implements Bird {

    private static final Logger logger = LoggerFactory.getLogger(Sparrow.class);

    @Override
    public void sayHello() {
        logger.info("Sparrow say hello");
    }
}
