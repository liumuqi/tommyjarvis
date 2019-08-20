package origin.spring.messageSend;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author:qishan
 * @Date: 2019-08-05
 * @Desc:
 **/
@Component
public class MockDownloadComponent {

    @Async // (A)
    public void mockDownload(String type, String sessionid) {
        for (int i = 0; i < 100; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(100); // (B)

                int percent = i + 1;
                String content = String.format("{\"username\":\"%s\",\"percent\":%d}", sessionid, percent); // (C)

                switch (type) { // (D)
                    case "sse":
                        SseNotificationController.usesSsePush(sessionid, content);
                        break;
                    case "ws":
                        WebSocketNotificationHandler.usesWSPush(sessionid, content);
                        break;
                    case "stomp":
//                        this.usesStompPush(sessionid, content);
                        break;
                    default:
                        throw new UnsupportedOperationException("");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

