package origin.spring.messageSend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @Author:qishan
 * @Date: 2019-08-05
 * @Desc:
 **/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        WebSocketNotificationHandler notificationHandler = new WebSocketNotificationHandler();

        registry.addHandler(notificationHandler, "/ws-notification") // (A)
                .addInterceptors(new HttpSessionHandshakeInterceptor())  // (B)
                .withSockJS();  // (C)
    }
}
