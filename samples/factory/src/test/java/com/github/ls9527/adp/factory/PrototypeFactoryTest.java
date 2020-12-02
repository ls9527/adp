package com.github.ls9527.adp.factory;

import com.github.ls9527.adp.adp.annotation.AdpResource;
import com.github.ls9527.adp.adp.context.Factory;
import com.github.ls9527.adp.factory.group.GroupFactoryConstants;
import com.github.ls9527.adp.factory.prototype.PrototypeBird;
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
    @AdpResource
    private Factory<PrototypeBird> allFactory;

    @Test
    public void sparrow() {
        PrototypeBird bird = allFactory.getBean(GroupFactoryConstants.SPARROW);
        bird.sayHello();

        PrototypeBird bird2 = allFactory.getBean(GroupFactoryConstants.SPARROW);
        bird2.sayHello();

    }


    @Test
    public void woodpecker() {
        PrototypeBird bird = allFactory.getBean(GroupFactoryConstants.WOODPECKER);
        bird.sayHello();

        PrototypeBird bird2 = allFactory.getBean(GroupFactoryConstants.WOODPECKER);
        bird2.sayHello();

    }

}