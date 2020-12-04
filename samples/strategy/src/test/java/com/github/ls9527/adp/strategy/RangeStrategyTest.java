package com.github.ls9527.adp.strategy;

import com.github.ls9527.adp.annotation.StrategyResource;
import com.github.ls9527.adp.strategy.range.demo.RangeBird;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrategyStarter.class)
public class RangeStrategyTest {

    @StrategyResource
    private RangeBird rangeBird;

    @Test
    public void sparrow() {
        rangeBird.sayHello(10);
    }

    @Test
    public void woodpecker() {
        rangeBird.sayHello(20);
    }


}
