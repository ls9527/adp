package com.github.ls9527.adp.factory.prototype;


import com.github.ls9527.adp.annotation.AdpFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


/**
 * @author ls9527
 */
@AdpFactory(name = PrototypeConstants.WOODPECKER)
@Service
@Scope("prototype")
public class PrototypeWoodpecker implements PrototypeBird {

    private static final Logger logger = LoggerFactory.getLogger(PrototypeWoodpecker.class);

    @Override
    public void sayHello() {
        logger.info("Woodpecker say hello, this hash code is :" + this.hashCode());
    }

}
