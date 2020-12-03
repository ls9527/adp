package com.github.ls9527.adp.strategy;

import com.github.ls9527.adp.annotation.FactoryResource;
import com.github.ls9527.adp.context.Factory;
import com.github.ls9527.adp.strategy.group.GroupBird;
import com.github.ls9527.adp.strategy.group.GroupFactoryConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrategyStarter.class)
public class GroupFactoryTest {

    @FactoryResource(group = "groupA")
    private Factory<GroupBird> groupABirdFactory;

    @FactoryResource(group = "groupB")
    private Factory<GroupBird> groupBBirdFactory;

    /**
     * groupAll
     */
    @FactoryResource
    private Factory<GroupBird> allFactory;
    /**
     * the cache of allFactory
     */
    @FactoryResource
    private Factory<GroupBird> sameAllFactory;

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
