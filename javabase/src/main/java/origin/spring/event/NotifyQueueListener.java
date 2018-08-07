package origin.spring.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author muqi.lmq
 * @date 2018/6/11.
 */
@Component
public class NotifyQueueListener {
    @EventListener
    public void consumerA(NotifyEvent notifyEvent) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("A: " + Thread.currentThread().getName() + " | " + notifyEvent.getMsg());
    }

    @EventListener
    public void consumerB(NotifyEvent notifyEvent) {
        System.out.println("B: " + Thread.currentThread().getName() + " | " + notifyEvent.getMsg());
    }


    @EventListener
    public void consumerC(NotifyEvent notifyEvent) {
        System.out.println("C: " + Thread.currentThread().getName() + " | " + notifyEvent.getMsg());
    }
}
