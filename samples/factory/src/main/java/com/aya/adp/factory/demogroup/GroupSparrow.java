package com.aya.adp.factory.demogroup;

import com.aya.adp.annotation.AdpFactory;
import com.aya.adp.annotation.AdpGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@AdpGroup(group = "groupA")
@AdpFactory(name = GroupFactoryConstants.SPARROW)
@Service
public class GroupSparrow implements GroupBird {

    private static final Logger logger = LoggerFactory.getLogger(GroupSparrow.class);

    @Override
    public void sayHello() {
        logger.info("Sparrow say hello");
    }

}
