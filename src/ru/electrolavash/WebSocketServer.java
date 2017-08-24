package ru.electrolavash;

import javax.faces.bean.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import ru.electrolavash.SocketMessage;


@ApplicationScoped
@ServerEndpoint("/log")
public class WebSocketServer {

    private static final Gson gson = new Gson();

    @OnOpen
    public void open(Session session) {
            System.out.println("open | " + WebSocketServer.this);
    }

    @OnClose
    public void close(Session session) {
        SessionHandler.dismissSession(session);
        System.out.println("close");
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("onError");
        System.out.println(error.getMessage());
    }

    @OnMessage
    public void handleMessage(String json, Session session) {
        System.out.println("handleMessage: " + json);
        final SocketMessage sMessage = gson.fromJson(json, SocketMessage.class);
        switch (sMessage.action) {
            case SocketMessage.REG:
                SessionHandler.registerSession(sMessage.data, session);
                break;
        }
    }
}
