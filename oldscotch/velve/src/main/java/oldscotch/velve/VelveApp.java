package oldscotch.velve;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.Application;

/**
 */
public class VelveApp extends Application {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private final CountDownLatch latch = new CountDownLatch(1);
    private final Set singletons = new HashSet<>();

    public VelveApp(boolean useVelve) {
        if (useVelve) {
            VelvePi pi = new VelvePi();
            executor.submit(pi);
            singletons.add(new VelveResource(latch, pi));
        } else {
            singletons.add(new VelveResource(latch, null));
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public Set<Object> getSingletons() {
        return singletons;
    }

}
