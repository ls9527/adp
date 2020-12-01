package com.aya.adp.factory.demogroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author ls9527
 */
@Service
public class GroupWoodpecker implements GroupBird {

    private static final Logger logger = LoggerFactory.getLogger(GroupWoodpecker.class);

    @Override
    public void sayHello() {
        logger.info("Woodpecker say hello");
    }

    public String getName() {
        return GroupFactoryConstants.WOODPECKER;
    }
}
