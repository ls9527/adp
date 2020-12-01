package com.aya.adp.factory;

import com.aya.adp.annotation.AdpResource;
import com.aya.adp.factory.demo.Bird;
import com.aya.adp.factory.demo.FactoryConstants;
import com.aya.adp.module.factory.DpFactories;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class FactoryTest {

    @Resource
    private List<Bird> birdList;

    @AdpResource
    private DpFactories<Bird> dpFactories;

    @Test
    public void sparrow() {
        Bird bird = dpFactories.getGroupBean(FactoryConstants.SPARROW);
        bird.sayHello();
    }

    @Test
    public void woodpecker() {
        Bird bird = dpFactories.getGroupBean(FactoryConstants.WOODPECKER);
        bird.sayHello();
    }
}
