package com.github.ls9527.adp.factory.group;


import com.github.ls9527.adp.annotation.AdpFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@AdpFactory(name = GroupFactoryConstants.SPARROW,group = "groupA")
@Service
public class GroupSparrow implements GroupBird {

    private static final Logger logger = LoggerFactory.getLogger(GroupSparrow.class);

    @Override
    public void sayHello() {
        logger.info("Sparrow say hello");
    }

}
