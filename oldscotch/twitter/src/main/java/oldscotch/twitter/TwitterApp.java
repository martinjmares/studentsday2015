package oldscotch.twitter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.core.Application;

/**
 */
public class TwitterApp extends Application {

    private final CountDownLatch latch = new CountDownLatch(1);

    private final Set singletons = new HashSet<>();

    public TwitterApp() {
        singletons.add(new TwitterResource(latch));
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public Set<Object> getSingletons() {
        return singletons;
    }
}
