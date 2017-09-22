package ru.electrolavash.blur;

import javax.faces.bean.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/blur_tag_logging")
public class WebSocketServer {

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
    public void handleMessage(String message, Session session) {
        final String[] split = message.split(":");
        if(split.length == 2){
            final String action = split[0];
            final String data = split[1];
            switch (action) {
                case SocketMessage.REG:
                    SessionHandler.registerSession(data, session);
                    break;
            }
        }
    }
}
