package com.github.ls9527.adp.factory;

import com.github.ls9527.adp.annotation.FactoryResource;
import com.github.ls9527.adp.context.Factory;
import com.github.ls9527.adp.factory.demo.Bird;
import com.github.ls9527.adp.factory.demo.FactoryConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class FactoryTest {

    @FactoryResource
    private Factory<Bird> factory;

    @Test
    public void sparrow() {
        Bird bird = factory.getBean(FactoryConstants.SPARROW);
        bird.sayHello();
    }

    @Test
    public void woodpecker() {
        Bird bird = factory.getBean(FactoryConstants.WOODPECKER);
        bird.sayHello();
    }
}
