package origin.base.inherits;


import org.joda.time.format.DateTimeFormat;

import java.util.Date;

/**
 * @Author:lmq
 * @Date: 2020/4/10
 * @Desc:
 **/
public class M {
    public static void main(String[] args) {
        TestA testA = new TestA();
        TestB testB  = new TestB();
        System.out.println(testA.getPushDataCount().getAndIncrement());
        System.out.println(testA.getPushDataCount().get());
        System.out.println(testB.getPushDataCount().get());
        Date yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime("19831201").toDate();
        System.out.println(yyyyMMdd);

    }
}
