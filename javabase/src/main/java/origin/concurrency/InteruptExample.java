package origin.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author:qishan
 * @Date: 2019-05-09
 * @Desc:
 **/
public class InteruptExample {

    private ReentrantLock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    public static void main(String[] args) throws Exception {
        Thread t = new Thread(new Runnable() {
            public void run() {
                InteruptExample test = new InteruptExample();
                test.say();
            }
        });
        t.start();
        t.interrupt();
//        for (int i = 0; i < 10; i++) {
//            new Thread(new Runnable() {
//                public void run() {
//                    InteruptExample test = new InteruptExample();
//                    test.say();
//                }
//            }).start();
//        }
    }

    public void say() {
        try {
            lock.lock();
            int i=0;
            while (i<3) {
                try {
                    i++;
                    condition.await();
                } catch (InterruptedException e) {
                    System.out.println("interrupted");
                    Thread.currentThread().interrupt();//问题.会一直循环
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
