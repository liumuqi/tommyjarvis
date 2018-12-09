package origin.concurrency;

import java.util.concurrent.TimeUnit;

/**
 * wait 必须获得锁,sleep则不用
 */
public class TestWaitSleep {
    private static Object m = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread thread_a = new Thread(() -> {
            synchronized (m) {
                try {
                    System.out.println("======m wait===============");
                    m.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("a error");
                }
            }
        });
        thread_a.start();
        Thread thread_b = new Thread(() -> {
            try {
                System.out.println("==============m'  wait=====");
                synchronized (m) {
                    m.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("b error");
            }
            System.out.println("==============m'  wait=====");
        });
        TimeUnit.SECONDS.sleep(10);
        synchronized (m) {
            m.notifyAll();
        }
        thread_b.start();
        TimeUnit.SECONDS.sleep(20);
    }
}
