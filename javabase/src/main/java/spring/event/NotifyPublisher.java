package spring.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author muqi.lmq
 * @date 2018/6/11.
 */
@Component
public class NotifyPublisher implements ApplicationContextAware {
    private ApplicationContext apc;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.apc = applicationContext;
    }

    // 发布一个消息
    public void publishEvent(int status, String msg) {
        if (status == 0) {
            apc.publishEvent(new NotifyEvent(this, msg));
        } else {
            apc.publishEvent(new NotifyEvent(this, msg));
        }
    }
}
