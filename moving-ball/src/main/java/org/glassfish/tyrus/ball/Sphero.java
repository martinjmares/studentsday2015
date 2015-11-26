package org.glassfish.tyrus.ball;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by petr on 23/11/15.
 */
public class Sphero {

    private static Session session;

    public static synchronized void sendMoveEvent(Event.EVENT_TYPE event) {
        if (session == null) {
            System.out.println("Sphero connection not started");
            return;
        }

        if (!session.isOpen()) {
            System.out.println("Sphero connection closed.");
            return;
        }

        System.out.println("Sending instruction:" + event);
        session.getAsyncRemote().sendText(event.toString(), sendResult -> {
            if (!sendResult.isOK()) {
                sendResult.getException().printStackTrace();
            }
        });
    }


    @ServerEndpoint("/sphero")
    public static class SpheroEndpoint {

        @OnOpen
        public void onOpen(Session session) {
            Sphero.session = session;
        }
    }
}
