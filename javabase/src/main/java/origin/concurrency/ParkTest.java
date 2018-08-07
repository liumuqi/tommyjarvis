package origin.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author muqi.lmq
 * @date 2018/4/27.
 */
public class ParkTest {
    private static volatile int x=0;
    public static void main(String[] args) throws InterruptedException {
        Thread thread_a = new Thread(()->{
            System.out.println(">>>thread_a park");
            if (x==1) {
                
            }
            LockSupport.park();
            System.out.println("a");
        });

        Thread thread_b = new Thread(()->{
            System.out.println(">>>thread_b park");
            LockSupport.park();
            LockSupport.unpark(thread_a);
            System.out.println("b");
        });
        TimeUnit.SECONDS.sleep(5);
        System.out.println("===begin unpark thread_b===");
        LockSupport.unpark(thread_b);
        LockSupport.unpark(thread_a);
        thread_a.start();
        thread_b.start();
        System.out.println("===end===");
        thread_a.join();
        thread_b.join();
    }
}
