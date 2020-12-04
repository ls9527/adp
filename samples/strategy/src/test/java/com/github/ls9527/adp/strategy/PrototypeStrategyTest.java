package com.github.ls9527.adp.strategy;

import com.github.ls9527.adp.annotation.StrategyResource;
import com.github.ls9527.adp.strategy.prototype.PrototypeBird;
import com.github.ls9527.adp.strategy.prototype.PrototypeConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrategyStarter.class)
public class PrototypeStrategyTest {

    /**
     * groupAll
     */
    @StrategyResource
    private PrototypeBird allStrategy;

    @Test
    public void sparrow() {
        allStrategy.sayHello(PrototypeConstants.SPARROW);

        allStrategy.sayHello(PrototypeConstants.SPARROW);
    }


    @Test
    public void woodpecker() {
        allStrategy.sayHello(PrototypeConstants.WOODPECKER);

        allStrategy.sayHello(PrototypeConstants.WOODPECKER);
    }

}
