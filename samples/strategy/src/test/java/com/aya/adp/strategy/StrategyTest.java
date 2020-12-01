package com.aya.adp.strategy;

import com.aya.adp.strategy.demo.Bird;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrategyStarter.class)
public class StrategyTest {

    private Bird bird;

    @Test
    public void sparrowSayHello() {
        bird.sayHello("SPARROW");
    }

    @Test
    public void woodpeckerSayHello() {
        bird.sayHello("WOODPECKER");
    }

    @Test
    public void sparrowVipLevel() {
        bird.vipLevel(1000);
    }

    @Test
    public void woodpeckerVipLevel() {
        bird.vipLevel(3000);
    }
}
