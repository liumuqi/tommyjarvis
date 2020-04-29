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
        System.out.println(testA.getPushDataCount().toString());
        System.out.println(testB.getPushDataCount().toString());
        Test testA1 = testB;
        System.out.println(testA1.getPushDataCount().toString());

        testA.setPd();

        testB.setPd();

        System.out.println(testA.getPushDataCount().toString());
        System.out.println(testB.getPushDataCount().toString());

        System.out.println(testA1.getPushDataCount());
        TestA testAa = new TestA();
        System.out.println(testAa.getPushDataCount());


        Date yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime("19831201").toDate();
        System.out.println(yyyyMMdd);

    }
}
