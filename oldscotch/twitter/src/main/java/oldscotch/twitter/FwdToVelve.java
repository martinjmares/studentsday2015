package oldscotch.twitter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class FwdToVelve implements TweetListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FwdToVelve.class);

    private final Client client;

    public FwdToVelve() {
        client = ClientBuilder.newBuilder().build();
    }

    @Override
    public void message(String author, String message) {
        LOGGER.info("Forward to velve!");
        try {
            client.target("http://192.168.1.150:3333/velve/tweet")
                    .queryParam("user", author)
                    .request()
                    .post(Entity.text(message));
            LOGGER.info("Forward to velve! - done");
        } catch (Exception e) {
            LOGGER.warn("Cannot forward to velve!", e);
        }
    }
}
