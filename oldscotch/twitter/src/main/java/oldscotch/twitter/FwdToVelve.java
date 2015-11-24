package oldscotch.twitter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

/**
 */
public class FwdToVelve implements TweetListener {

    private final Client client;

    public FwdToVelve() {
        client = ClientBuilder.newBuilder().build();
    }

    @Override
    public void message(String author, String message) {
        client.target("http://192.168.1.150/velve/tweet")
                .queryParam("user", author)
                .request()
                .post(Entity.text(message));
    }
}
