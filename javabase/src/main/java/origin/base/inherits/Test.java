package origin.base.inherits;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:lmq
 * @Date: 2020/4/10
 * @Desc:
 **/
public abstract class Test {
   protected int pushDataCount = 10;

    public Object getPushDataCount() {
        return pushDataCount;
    }

    public void setPushDataCount(int pushDataCount) {
        this.pushDataCount = pushDataCount;
    }
}
