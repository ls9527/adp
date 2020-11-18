package com.aya.adp.factory;

import com.aya.adp.annotation.AdpResource;
import com.aya.adp.factory.demo.Bird;
import com.aya.adp.factory.demo.FactoryConstants;
import com.aya.adp.module.factory.DpFactories;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class FactoryTest {

    @AdpResource(group = FactoryConstants.GROUP_BIRD, classes=Bird.class)
    private DpFactories dpFactories;

    @AdpResource(group = FactoryConstants.GROUP_BIRD, classes=Bird.class)
    private DpFactories dpFactories2;

    @AdpResource(group = FactoryConstants.GROUP_BIRD, classes=Bird.class)
    private DpFactories dpFactories3;
    @Test
    public void sparrow() {
        Bird bird = dpFactories.getBean(FactoryConstants.SPARROW);
        bird.sayHello();
    }

    @Test
    public void woodpecker() {
        Bird bird = dpFactories.getBean(FactoryConstants.WOODPECKER);
        bird.sayHello();
    }
}
