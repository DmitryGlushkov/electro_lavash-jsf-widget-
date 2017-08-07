package ru.electrolavash;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/log")
public class WebSocketServer {

    @OnOpen
    public void open(Session session) {
        SessionHandler.onSessionOpened(session);
        System.out.println("open | " + session.getId());
        System.out.println("open | " + WebSocketServer.this);
    }

    @OnClose
    public void close(Session session) {
        System.out.println("close");
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("onError");
        System.out.println(error.getMessage());
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        System.out.println("handleMessage | " + message);
    }
}
