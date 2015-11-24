package oldscotch.twitter;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 */
@Path("/")
public class TwitterResource {

    private final CountDownLatch stopLatch;
    private final Main main;

    public TwitterResource(CountDownLatch stopLatch, Main main) {
        this.stopLatch = stopLatch;
        this.main = main;
    }

    @Path("stop")
    @POST
    public void stop() {
        stopLatch.countDown();
    }

    @Path("tweet")
    @POST
    @Consumes("text/plain")
    @Produces("text/plain")
    public String tweet(String message) {
        main.fireMessage("console", message);
        return "OK";
    }
}
