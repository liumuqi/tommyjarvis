package origin.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author muqi.lmq
 * @date 2018/6/11.
 */
public class NotifyEvent extends ApplicationEvent {
    private String msg;

    public NotifyEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

