package ru.electrolavash;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionHandler {

    private static final Map<String, Session> sessionsMap = new ConcurrentHashMap<>();

    public static void registerSession(final String id, final Session session) {
        sessionsMap.put(id, session);
    }

    public static void send(final String id, final String message) {
        try {
            final Session session = sessionsMap.get(id);
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dismissSession(final Session session) {
        System.out.println("dismissSession");
        String id = null;
        for (final Map.Entry<String, Session> e : sessionsMap.entrySet()) {
            if (e.getValue() == session) {
                id = e.getKey();
                break;
            }
        }
        if (id != null) sessionsMap.remove(id);
        System.out.println("" + sessionsMap.size());
    }

}
