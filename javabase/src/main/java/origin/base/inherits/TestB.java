package origin.base.inherits;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:lmq
 * @Date: 2020/4/10
 * @Desc:
 **/
public class TestB extends Test{
    AtomicInteger pushDataCount = new AtomicInteger(10);

    @Override
    public AtomicInteger getPushDataCount() {
        return super.pushDataCount;
    }
}
