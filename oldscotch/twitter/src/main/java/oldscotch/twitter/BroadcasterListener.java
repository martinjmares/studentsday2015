package oldscotch.twitter;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;

/**
 */
public class BroadcasterListener implements TweetListener{

    private final SseBroadcaster sseBroadcaster = new SseBroadcaster();
    private final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

    @Override
    public void message(String author, String message) {
        OutboundEvent event = eventBuilder.name("tweet")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, message)
                .build();
        sseBroadcaster.broadcast(event);
    }

    public void registerNewClient(EventOutput eventOutput) {
        sseBroadcaster.add(eventOutput);
        OutboundEvent event = eventBuilder.name("new-client")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, "One more client")
                .build();
        sseBroadcaster.broadcast(event);
    }
}
