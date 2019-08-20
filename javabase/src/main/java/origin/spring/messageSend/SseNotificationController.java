package origin.spring.messageSend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author:qishan
 * @Date: 2019-08-05
 * @Desc:
 **/
@RestController
public class SseNotificationController {

    public static final Map<String, SseEmitter> SSE_HOLDER = new ConcurrentHashMap<>(); // (A)

    /**
     * 通过sessionId获取对应的客户端进行推送消息
     */
    public static void usesSsePush(String sessionid, String content) {  // (C)
        SseEmitter emitter = SseNotificationController.SSE_HOLDER.get(sessionid);
        if (Objects.nonNull(emitter)) {
            try {
                emitter.send(content);
            } catch (IOException | IllegalStateException e) {
                SseNotificationController.SSE_HOLDER.remove(sessionid);
            }
        }
    }

    /*
     usesSSENotification: function () {
                var tt = this;
                var url = "/api/sse-notification";
                var sseClient = new EventSource(url);  // (A)
                sseClient.onopen = function () {...}; // (B)

                sseClient.onmessage = function (msg) {   // (C)
                    var jsonStr = msg.data;
                    console.log('message', jsonStr);
                    var obj = JSON.parse(jsonStr);
                    var percent = obj.percent;
                    tt.sseMsg += 'SSE 通知您:已下载完成' + percent + "%\r\n";
                    if (percent === 100) {
                        sseClient.close();  // (D)
                    }
                };
                sseClient.onerror = function () {
                    console.log("EventSource failed.");
                };
            }
     */
    @GetMapping("/api/sse-notification")
    public SseEmitter files(HttpServletRequest request) {
        long millis = TimeUnit.SECONDS.toMillis(60);
        SseEmitter sseEmitter = new SseEmitter(millis); // (B)

        HttpSession session = request.getSession();
        String sessionid = session.getId();

        SSE_HOLDER.put(sessionid, sseEmitter);
        return sseEmitter;
    }

}

