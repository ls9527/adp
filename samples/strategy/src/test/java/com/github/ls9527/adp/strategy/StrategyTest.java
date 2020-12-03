package com.github.ls9527.adp.strategy;

import com.github.ls9527.adp.annotation.StrategyResource;
import com.github.ls9527.adp.strategy.demo.Bird;
import com.github.ls9527.adp.strategy.demo.FactoryConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrategyStarter.class)
public class StrategyTest {

    @StrategyResource
    private Bird bird;

    @Test
    public void sparrow() {
        bird.sayHello(FactoryConstants.SPARROW, 10);
    }

    @Test
    public void woodpecker() {
        bird.sayHello(FactoryConstants.WOODPECKER, 20);
    }
}
