package ru.electrolavash.blur;

import javax.faces.bean.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import jdk.nashorn.internal.parser.JSONParser;

@ApplicationScoped
@ServerEndpoint("/blur_tag_logging")
public class WebSocketServer {

    private static final Gson gson = new Gson();

    @OnOpen
    public void open(Session session) {
    }

    @OnClose
    public void close(Session session) {
        SessionHandler.dismissSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println(error.getMessage());
    }

    @OnMessage
    public void handleMessage(String json, Session session) {
        final SocketMessage sMessage = gson.fromJson(json, SocketMessage.class);
        switch (sMessage.action) {
            case SocketMessage.REG:
                SessionHandler.registerSession(sMessage.data, session);
                break;
        }
    }
}
