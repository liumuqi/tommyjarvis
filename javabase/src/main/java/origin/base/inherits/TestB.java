package origin.base.inherits;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:lmq
 * @Date: 2020/4/10
 * @Desc:
 **/
public class TestB extends Test{
//    AtomicInteger pushDataCount = new AtomicInteger(20);

    @Override
    public Object getPushDataCount() {
        return pushDataCount;
    }
    public void setPd(){
        pushDataCount = 111;
    }
}
