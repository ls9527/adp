package com.aya.adp.factory;

import com.aya.adp.annotation.AdpResource;
import com.aya.adp.factory.group.GroupBird;
import com.aya.adp.factory.group.GroupFactoryConstants;
import com.aya.adp.module.factory.Factory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class GroupFactoryTest {

    @AdpResource(group = "groupA")
    private Factory<GroupBird> groupABirdFactory;

    @AdpResource(group = "groupB")
    private Factory<GroupBird> groupBBirdFactory;

    /**
     * groupAll
     */
    @AdpResource
    private Factory<GroupBird> allFactory;

    @Test
    public void sparrow() {
        GroupBird bird = groupABirdFactory.getBean(GroupFactoryConstants.SPARROW);
        bird.sayHello();
    }


    @Test
    public void sparrowNotExists() {
        GroupBird bird = groupBBirdFactory.getBean(GroupFactoryConstants.SPARROW);
        Assert.assertNull(bird);
    }


    @Test
    public void woodpecker() {
        GroupBird bird = groupBBirdFactory.getBean(GroupFactoryConstants.WOODPECKER);
        bird.sayHello();
    }


    @Test
    public void woodpeckerNotExists() {
        GroupBird bird = groupABirdFactory.getBean(GroupFactoryConstants.WOODPECKER);
        Assert.assertNull(bird);
    }

    @Test
    public void allExists() {
        GroupBird woodpecker = allFactory.getBean(GroupFactoryConstants.WOODPECKER);
        Assert.assertNotNull(woodpecker);

        GroupBird sparrow = allFactory.getBean(GroupFactoryConstants.SPARROW);
        Assert.assertNotNull(sparrow);
    }
}
