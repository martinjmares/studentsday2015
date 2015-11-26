package org.glassfish.tyrus.ball;

/**
 * Created by petr on 21/11/15.
 */
public class Event {

    private final String eventType;
    private final String payload;

    public Event(final EVENT_TYPE eventType, String payload) {
        this.eventType = eventType.toString();
        this.payload = payload;
    }

    public Event(final EVENT_TYPE eventType) {
        this.eventType = eventType.toString();
        this.payload = null;
    }

    public EVENT_TYPE getEventType() {
        return EVENT_TYPE.valueOf(eventType);
    }

    public String getPayload() {
        return payload;
    }

    public enum EVENT_TYPE {
        LEFT_DOWN,
        LEFT_UP,
        UP_DOWN,
        UP_UP,
        RIGHT_DOWN,
        RIGHT_UP,
        DOWN_DOWN,
        DOWN_UP,
        USER_CHANGE,
        ENABLE_CONTROL,
        DISABLE_CONTROL,
        USER_SUBMIT,
        REQUEST_SLOT,
        QUEUE_UPDATE,
        DUPLICATE_NAME
    }
}
