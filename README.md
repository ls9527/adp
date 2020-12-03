use design pattern in spring 

factory:
```java
public interface Bird {
    void sayHello();
}


@AdpFactory(name = FactoryConstants.SPARROW)
@Service
public class Sparrow implements Bird {
    private static final Logger logger = LoggerFactory.getLogger(Sparrow.class);
    @Override
    public void sayHello() {
        logger.info("Sparrow say hello");
    }
}

@AdpFactory(name = FactoryConstants.WOODPECKER)
@Service
public class Woodpecker implements Bird {
    private static final Logger logger = LoggerFactory.getLogger(Woodpecker.class);
    @Override
    public void sayHello() {
        logger.info("Woodpecker say hello");
    }
}

public class FactoryConstants {
    public static final String SPARROW = "SPARROW";
    public static final String WOODPECKER = "WOODPECKER";
}

```

test:
```java
public class FactoryTest {
    @FactoryResource
    private Factory<Bird> factory;

    @Test
    public void sparrow() {
        Bird bird = factory.getBean(FactoryConstants.SPARROW);
        bird.sayHello();
    }
}
```