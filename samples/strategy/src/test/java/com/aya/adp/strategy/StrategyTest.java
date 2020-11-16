package com.aya.adp.strategy;

import com.aya.adp.annotation.AdpStrategy;
import com.aya.adp.module.factory.DpFactories;
import com.aya.adp.strategy.demo.Bird;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrategyStarter.class)
public class StrategyTest {

    @AdpStrategy(spel = {"#{type}"})
    @Resource
    private Bird bird;

    @Test
    public void sparrow() {
        bird.sayHello("SPARROW");
    }

    @Test
    public void woodpecker() {
        bird.sayHello("WOODPECKER");
    }
}
