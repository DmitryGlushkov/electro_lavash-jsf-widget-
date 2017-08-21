package ru.electrolavash;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionHandler {

    private static final Map<String, Session> sessionsMap = new HashMap<>();

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

}
