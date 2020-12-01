package com.aya.adp.factory;

import com.aya.adp.annotation.AdpResource;
import com.aya.adp.factory.demogroup.GroupBird;
import com.aya.adp.factory.demogroup.GroupFactoryConstants;
import com.aya.adp.module.factory.DpFactories;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FactoryStarter.class)
public class GroupFactoryTest {

    @AdpResource
    private DpFactories<GroupBird> dpFactories;

    @Test
    public void sparrow() {
        GroupBird bird = dpFactories.getGroupBean(GroupFactoryConstants.SPARROW);
        bird.sayHello();
    }

    @Test
    public void woodpecker() {
        GroupBird bird = dpFactories.getGroupBean(GroupFactoryConstants.WOODPECKER);
        bird.sayHello();
    }
}
