package com.aya.adp.factory;

import com.aya.adp.annotation.AdpResource;
import com.aya.adp.factory.demogroup.GroupBird;
import com.aya.adp.factory.demogroup.GroupFactoryConstants;
import com.aya.adp.module.factory.DpFactories;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class GroupFactoryTest {

    @AdpResource(group = "groupA")
    private DpFactories<GroupBird> groupABirdDpFactories;

    @AdpResource(group = "groupB")
    private DpFactories<GroupBird> groupBBirdDpFactories;

    /**
     * groupAll
     */
    @AdpResource
    private DpFactories<GroupBird> allDpFactories;

    @Test
    public void sparrow() {
        GroupBird bird = groupABirdDpFactories.getGroupBean(GroupFactoryConstants.SPARROW);
        bird.sayHello();
    }


    @Test
    public void sparrowNotExists() {
        GroupBird bird = groupBBirdDpFactories.getGroupBean(GroupFactoryConstants.SPARROW);
        Assert.assertNull(bird);
    }


    @Test
    public void woodpecker() {
        GroupBird bird = groupBBirdDpFactories.getGroupBean(GroupFactoryConstants.WOODPECKER);
        bird.sayHello();
    }


    @Test
    public void woodpeckerNotExists() {
        GroupBird bird = groupABirdDpFactories.getGroupBean(GroupFactoryConstants.WOODPECKER);
        Assert.assertNull(bird);
    }

    @Test
    public void allExists() {
        GroupBird woodpecker = allDpFactories.getGroupBean(GroupFactoryConstants.WOODPECKER);
        Assert.assertNotNull(woodpecker);

        GroupBird sparrow = allDpFactories.getGroupBean(GroupFactoryConstants.SPARROW);
        Assert.assertNotNull(sparrow);
    }
}
