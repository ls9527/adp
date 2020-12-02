package com.aya.adp.factory.prototype;

import com.aya.adp.annotation.AdpFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@AdpFactory(name = PrototypeConstants.SPARROW)
@Service
@Scope("prototype")
public class PrototypeSparrow implements PrototypeBird {

    private static final Logger logger = LoggerFactory.getLogger(PrototypeSparrow.class);

    @Override
    public void sayHello() {
        logger.info("Sparrow say hello, this hash code is :" + this.hashCode());
    }

}
