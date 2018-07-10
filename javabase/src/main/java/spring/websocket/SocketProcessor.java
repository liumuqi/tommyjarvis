package spring.websocket;

import com.google.common.eventbus.Subscribe;
import eventbus.EventSubProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import utils.Jacksons;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author muqi.lmq
 * @date 2018/7/3.
 */
@Component
public class SocketProcessor extends TextWebSocketHandler {
    static List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public SocketProcessor() {
    }

    @PostConstruct
    private void init() {
        EventSubProcessor.register(this);
    }

    @Subscribe
    public void subscribeEvent(Object jobView) {
        String jobstatus_json = null;
        jobstatus_json = Jacksons.transObjectToString(jobView);

        for (WebSocketSession webSocketSession : sessions) {
            try {
                webSocketSession.sendMessage(new TextMessage(jobstatus_json));
            } catch (Exception e) {
            }
        }
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the messages will be broadcasted to all users.
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
