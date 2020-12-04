package com.github.ls9527.adp.factory;

import com.github.ls9527.adp.annotation.FactoryResource;
import com.github.ls9527.adp.context.Factory;
import com.github.ls9527.adp.factory.prototype.PrototypeBird;
import com.github.ls9527.adp.factory.prototype.PrototypeConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class PrototypeFactoryTest {

    /**
     * groupAll
     */
    @FactoryResource
    private Factory<PrototypeBird> allFactory;

    @Test
    public void sparrow() {
        PrototypeBird bird = allFactory.getBean(PrototypeConstants.SPARROW);
        bird.sayHello();

        PrototypeBird bird2 = allFactory.getBean(PrototypeConstants.SPARROW);
        bird2.sayHello();

    }


    @Test
    public void woodpecker() {
        PrototypeBird bird = allFactory.getBean(PrototypeConstants.WOODPECKER);
        bird.sayHello();

        PrototypeBird bird2 = allFactory.getBean(PrototypeConstants.WOODPECKER);
        bird2.sayHello();

    }

}
