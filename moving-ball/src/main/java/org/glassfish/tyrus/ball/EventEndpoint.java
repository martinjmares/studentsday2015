package org.glassfish.tyrus.ball;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by petr on 21/11/15.
 */
@ServerEndpoint("/events")
public class EventEndpoint {

    private Session session;
    private TimeSlotScheduler.User user;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose() {
        if (user!= null) {
            TimeSlotScheduler.destroyUser(user);
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
        Gson gson = new GsonBuilder().create();
        Event event = gson.fromJson(message, Event.class);

        switch (event.getEventType()) {
            case LEFT_DOWN:
            case LEFT_UP:
            case UP_DOWN:
            case UP_UP:
            case RIGHT_DOWN:
            case RIGHT_UP:
            case DOWN_DOWN:
            case DOWN_UP: {
                handleMoveEvent(message, event);
                break;
            }

            case USER_SUBMIT: {
                user = TimeSlotScheduler.createUser(event.getPayload(), session);
                break;
            }

            case REQUEST_SLOT: {
                TimeSlotScheduler.schedule(user);
                break;
            }

            default: {
                throw new IllegalStateException("Unsupported event: " + message);
            }

        }
    }

    private void handleMoveEvent(String event, Event parsedEvent) {
        TimeSlotScheduler.User currentUser = TimeSlotScheduler.getCurrentUser();
        if (currentUser == null || !currentUser.equals(user)) {
            return;
        }

        Sphero.sendMoveEvent(parsedEvent.getEventType());
        session.getOpenSessions().stream().filter((s -> s != session)).forEach(s -> s.getAsyncRemote().sendText(event));
    }
}
