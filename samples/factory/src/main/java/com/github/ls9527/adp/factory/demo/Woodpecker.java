package com.github.ls9527.adp.factory.demo;


import com.github.ls9527.adp.adp.annotation.AdpFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author ls9527
 */
@AdpFactory(name = FactoryConstants.WOODPECKER)
@Service
public class Woodpecker implements Bird {

    private static final Logger logger = LoggerFactory.getLogger(Woodpecker.class);

    @Override
    public void sayHello() {
        logger.info("Woodpecker say hello");
    }
}
