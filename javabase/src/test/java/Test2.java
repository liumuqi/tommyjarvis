import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class Test2 {

    public static void main(String[] args) throws InterruptedException {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Runnable runnable = () -> {
                int a = 0, b = 1;
                for (int i1 = 0; i1 < 2000000000; i1++) {
                    a = a + b;
                    b = a - b;
                }
            };
            tasks.add(runnable);
        }
        runTasks(tasks);
    }

    static void runTasks(List<Runnable> tasks) throws InterruptedException {
        final Phaser phaser = new Phaser(1) {
            protected boolean onAdvance(int phase, int registeredParties) {
                System.out.println("phase: " + phase + " registerdParties: " + registeredParties);
                return phase >= 1 || registeredParties == 0;
            }
        };
        for (final Runnable task : tasks) {
            phaser.register();
            new Thread(() -> {
                do {
                    phaser.arriveAndAwaitAdvance();
                    task.run();
                } while (!phaser.isTerminated());
            }).start();
//            Thread.sleep(500);
        }
        phaser.arriveAndDeregister();
    }
//        Thread thread1 = new Thread(() -> {
//            synchronized (Test2.class) {
//                try {
//                    System.out.println(new Date() + " Thread1 is running");
////                    Thread.currentThread().sleep(20000);
//                    Test2.class.wait();
//                    System.out.println(new Date() + " Thread1 ended");
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//        thread1.start();
//
//        Thread thread2 = new Thread(() -> {
//            synchronized (Test2.class) {
//                try {
//                    System.out.println(new Date() + " Thread2 is running");
////                    Thread.sleep(2000);
//                    System.out.println(new Date() + " Thread2 ended");
//                    Test2.class.notify();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            for(long i = 0; i < 200000; i++) {
//                for(long j = 0; j < 100000; j++) {}
//            }
//        });

    // Don't use sleep method to avoid confusing
//        for(long i = 0; i < 200000; i++) {
//            for(long j = 0; j < 100000; j++) {}
//        }
//        thread2.start();
//}


//    public static void main(String[] args) {
//        testSynchronized();
//        testReentrantLock();
//    }

    public static void testReentrantLock() {
        final SampleSupport1 support = new SampleSupport1();
        Thread first = new Thread(() -> {
            try {
                support.doSomething();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("first Thread Interrupted without executing counter++,beacuse it waits a long time.");
            }
        }, "first");
        Thread second = new Thread(() -> {
            try {
                support.doSomething();
            } catch (InterruptedException e) {
                System.out.println("Second Thread Interrupted without executing counter++,beacuse it waits a long time.");
            }
        }, "second");
        executeTest(second, first);
    }

    public static void testSynchronized() {
        final SampleSupport2 support2 = new SampleSupport2();
        Runnable runnable = () -> support2.doSomething();
        Thread third = new Thread(runnable, "third");
        Thread fourth = new Thread(runnable, "fourth");
        executeTest(third, fourth);
    }

    /**
     * Make threads a run faster than threads b,
     * then threads b will be interruted after about 1s.
     *
     * @param a
     * @param b
     */
    public static void executeTest(Thread a, Thread b) {
        a.start();
        try {
            Thread.sleep(100);
            b.start();
            Thread.sleep(1000);
            b.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

abstract class SampleSupport {
    protected int counter;

    /**
     * A simple countdown,it will stop after about 5s.
     */
    public void startTheCountdown() {
        long currentTime = System.currentTimeMillis();
        for (; ; ) {
            long diff = System.currentTimeMillis() - currentTime;
            if (diff > 50000) {
                break;
            }
        }
    }
}

class SampleSupport1 extends SampleSupport {
    private final ReentrantLock lock = new ReentrantLock();

    public void doSomething() throws InterruptedException {
        lock.lockInterruptibly();
//        try {
//            lock.lockInterruptibly();
//        } catch (InterruptedException e) {
//            //做一些其它的事，不结束线程
//        }
        System.out.println(Thread.currentThread().getName() + " support 1 lock will execute counter++.");
        startTheCountdown();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }
}

class SampleSupport2 extends SampleSupport {
    public synchronized void doSomething() {
        System.out.println(Thread.currentThread().getName() + " support 2  sync will execute counter++.");
        startTheCountdown();
        counter++;
    }
}
