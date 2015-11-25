package oldscotch.growl;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final CountDownLatch latch = new CountDownLatch(1);
    private final Client client;
    private final Growler growler;

    public Main() throws IOException, InterruptedException {
        growler = new Growler();
        client = ClientBuilder.newBuilder()
                .register(SseFeature.class)
                .build();
    }

    private void run() throws Exception {
        LOGGER.info("About to listen.");
        WebTarget target = client.target("http://localhost:4444/tweet-stream");
        EventSource eventSource = new EventSource(target) {
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                if ("tweet".equals(inboundEvent.getName())) {
                    String msg = inboundEvent.readData(String.class);
                    LOGGER.info("New tweet: " + msg);
                    growler.tweet(msg);
                } else {
                    LOGGER.info("Unknown message: " + inboundEvent.getName()
                                        + ": " + inboundEvent.readData(String.class));
                }
            }
        };
        LOGGER.info("Initialized, waiting");
        latch.await();
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();
            m.run();
        } catch (Exception e) {
            LOGGER.error("Ooooops", e);
        }
    }
}
