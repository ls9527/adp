package com.github.ls9527.adp.strategy.prototype;


import com.github.ls9527.adp.annotation.AdpStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author ls9527
 */

@Service
@Scope("prototype")
public class PrototypeSparrow implements PrototypeBird {

    private static final Logger logger = LoggerFactory.getLogger(PrototypeSparrow.class);

    @AdpStrategy(condition = "#{T(com.github.ls9527.adp.strategy.demo.FactoryConstants).SPARROW eq type}")
    @Override
    public void sayHello(String type) {
        logger.info("Sparrow say hello, this hash code is :" + this.hashCode());
    }

}
