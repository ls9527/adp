package com.github.ls9527.adp.strategy.group;


import com.github.ls9527.adp.annotation.AdpFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author ls9527
 */
@AdpFactory(name = GroupFactoryConstants.WOODPECKER, group = "groupB")
@Service
public class GroupWoodpecker implements GroupBird {

    private static final Logger logger = LoggerFactory.getLogger(GroupWoodpecker.class);

    @Override
    public void sayHello() {
        logger.info("Woodpecker say hello");
    }

}
