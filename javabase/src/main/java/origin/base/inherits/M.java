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
        System.out.println("10 testA:"+testA.getPushDataCount().toString());
        System.out.println("10 testB:"+testB.getPushDataCount().toString());
        Test testA1 = testB;
        System.out.println("10 testA1:"+testA1.getPushDataCount().toString());

        testA.setPd();

        testB.setPd();

        System.out.println("333 testA:"+testA.getPushDataCount().toString());
        System.out.println("111 testB:"+testB.getPushDataCount().toString());

        System.out.println("111 testA1 ref testB:"+testA1.getPushDataCount());
        TestA testAa = new TestA();
        System.out.println("10 testAa:"+testAa.getPushDataCount());


        Date yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime("19831201").toDate();
        System.out.println(yyyyMMdd);

    }
}
