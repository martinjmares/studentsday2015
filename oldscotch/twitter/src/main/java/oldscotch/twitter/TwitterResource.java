package oldscotch.twitter;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 */
@Path("/")
public class TwitterResource {

    private final CountDownLatch stopLatch;

    public TwitterResource(CountDownLatch stopLatch) {
        this.stopLatch = stopLatch;
    }

    @Path("stop")
    @POST
    public void stop() {
        stopLatch.countDown();
    }
}
