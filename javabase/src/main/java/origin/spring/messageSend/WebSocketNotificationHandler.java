package origin.spring.messageSend;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:qishan
 * @Date: 2019-08-05
 * @Desc:
 **/
public class WebSocketNotificationHandler extends TextWebSocketHandler {


    public static final Map<String, WebSocketSession> WS_HOLDER = new ConcurrentHashMap<>();  // (A)

    public static void usesWSPush(String sessionid, String content) {    // (C)
        WebSocketSession wssession = WebSocketNotificationHandler.WS_HOLDER.get(sessionid);
        if (Objects.nonNull(wssession)) {
            TextMessage textMessage = new TextMessage(content);
            try {
                wssession.sendMessage(textMessage);
            } catch (IOException | IllegalStateException e) {
                WebSocketNotificationHandler.WS_HOLDER.remove(sessionid);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String httpSessionId = (String) session.getAttributes().get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);
        WS_HOLDER.remove(httpSessionId);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {   // (B)
        String httpSessionId = (String) session.getAttributes().get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);
        WS_HOLDER.put(httpSessionId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        log.info("handleTextMessage={}", message.getPayload());
    }
}

