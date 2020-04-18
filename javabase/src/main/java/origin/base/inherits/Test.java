package origin.base.inherits;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:lmq
 * @Date: 2020/4/10
 * @Desc:
 **/
public abstract class Test {
    AtomicInteger pushDataCount = new AtomicInteger(0);

    public AtomicInteger getPushDataCount() {
        return pushDataCount;
    }

    public void setPushDataCount(AtomicInteger pushDataCount) {
        this.pushDataCount = pushDataCount;
    }
}
