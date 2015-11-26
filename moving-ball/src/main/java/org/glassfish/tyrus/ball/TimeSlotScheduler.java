package org.glassfish.tyrus.ball;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.websocket.Session;

import com.google.gson.Gson;

/**
 * Created by petr on 21/11/15.
 */
public class TimeSlotScheduler {

    private static Set<String> namesInUse = new HashSet<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Gson gson = new Gson();
    private static final Queue<User> scheduledTimeSlots = new LinkedList<>();
    private static int idGenerator = 0;

    synchronized static User createUser(String name, Session session) {
        if (namesInUse.contains(name)) {
            Event duplicateNameEvent = new Event(Event.EVENT_TYPE.DUPLICATE_NAME, name);
            session.getAsyncRemote().sendText(gson.toJson(duplicateNameEvent));
            return null;
        }
        namesInUse.add(name);
        return new User(name, session);
    }

    synchronized static void schedule(User user) {
        scheduledTimeSlots.add(user);
        if (user.equals(scheduledTimeSlots.peek())) {
            scheduleNextUser();
            return;
        }

        sendQueueUpdate();
    }

    synchronized static void destroyUser(User user) {
        namesInUse.remove(user.name);
        if (scheduledTimeSlots.peek() != null && scheduledTimeSlots.peek().equals(user)) {
            onNext();
            return;
        }

        boolean removed = scheduledTimeSlots.remove(user);
        if (removed) {
            sendQueueUpdate();
        }
    }

    synchronized static User getCurrentUser() {
        return scheduledTimeSlots.peek();
    }

    private static void onNext() {
        User currentUser = scheduledTimeSlots.poll();
        if (currentUser != null) {
            Sphero.sendMoveEvent(Event.EVENT_TYPE.LEFT_UP);
            Sphero.sendMoveEvent(Event.EVENT_TYPE.UP_UP);
            Sphero.sendMoveEvent(Event.EVENT_TYPE.RIGHT_UP);
            Sphero.sendMoveEvent(Event.EVENT_TYPE.DOWN_UP);
            Event disableControlEvent = new Event(Event.EVENT_TYPE.DISABLE_CONTROL);
            currentUser.wsSession.getAsyncRemote().sendText(gson.toJson(disableControlEvent));
        }

        scheduleNextUser();
    }

    private static void scheduleNextUser() {
        User nextUser = scheduledTimeSlots.peek();
        if (nextUser == null) {
            return;
        }

        Event userChangeEvent = new Event(Event.EVENT_TYPE.USER_CHANGE, nextUser.name);
        nextUser.wsSession.getOpenSessions().forEach(s -> s.getAsyncRemote().sendText(gson.toJson(userChangeEvent)));
        Event enableControlEvent = new Event(Event.EVENT_TYPE.ENABLE_CONTROL);
        nextUser.wsSession.getAsyncRemote().sendText(gson.toJson(enableControlEvent));
        scheduler.schedule(() -> {
            synchronized (TimeSlotScheduler.class) {
                if (nextUser.equals(scheduledTimeSlots.peek())) {
                    onNext();
                }
            }
        }, 20, TimeUnit.SECONDS);
        sendQueueUpdate();
    }

    private static void sendQueueUpdate() {
        User head = scheduledTimeSlots.peek();
        List<String> list = scheduledTimeSlots.stream().map(u -> u.name).collect(Collectors.toList());
        String serializedQueue = gson.toJson(list);
        Event queueUpdateEvent = new Event(Event.EVENT_TYPE.QUEUE_UPDATE, serializedQueue);
        head.wsSession.getOpenSessions().forEach(s -> s.getAsyncRemote().sendText(gson.toJson(queueUpdateEvent)));
    }

    static final class User {

        private final String name;
        private final int id;
        private final Session wsSession;

        private User(String name, Session wsSession) {
            this.name = name;
            this.id = idGenerator++;
            this.wsSession = wsSession;
        }
    }
}
