package oldscotch.velve;

import java.util.concurrent.CountDownLatch;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@Path("/velve")
public class VelveResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelveResource.class);

    private final CountDownLatch stopLatch;
    private final VelvePi pi;

    public VelveResource(CountDownLatch stopLatch, VelvePi pi) {
        this.stopLatch = stopLatch;
        this.pi = pi;
    }

    @Path("tweet")
    @POST
    public void tweet(@QueryParam("user") @DefaultValue("unspecified") String user, String message) {
        System.out.println(user + ": " + message);
        if (pi != null) {
            pi.add();
        }
    }

    @Path("stop")
    @POST
    public void stop() {
        stopLatch.countDown();
    }
}
