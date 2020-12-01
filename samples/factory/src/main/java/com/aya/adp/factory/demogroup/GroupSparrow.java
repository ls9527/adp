package com.aya.adp.factory.demogroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@Service
public class GroupSparrow implements GroupBird {

    private static final Logger logger = LoggerFactory.getLogger(GroupSparrow.class);

    @Override
    public void sayHello() {
        logger.info("Sparrow say hello");
    }

    public String getName() {
        return GroupFactoryConstants.SPARROW;
    }
}
