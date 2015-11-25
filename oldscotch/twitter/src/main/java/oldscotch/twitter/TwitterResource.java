package oldscotch.twitter;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

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

    @Path("tweet-stream")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput listenToTweets() {
        final EventOutput eventOutput = new EventOutput();
        main.getBroadcasterListener().registerNewClient(eventOutput);
        return eventOutput;
    }

}
