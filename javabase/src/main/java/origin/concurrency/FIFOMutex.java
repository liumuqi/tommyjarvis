package origin.concurrency;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 不可重入的lock方法将请求锁的当前线程放入队列
 * 如果等待队列的队首元素就是当前线程，则当前线程修改变量locked的值为true表示已经上锁了。 然后删除等待锁队列中的队首元素也就是当前的线程。当前线程继续正常执行。这里需要注意一点，如果当前线程再次掉用lock方法则当前线程会被阻塞。这样就可能发生死锁，也就是说这个锁是不可重入的。
 * 如果等待队列的队首元素就不是当前线程（第一个获取锁的线程还没有执行remove）或上锁失败（第一个线程还没有释放锁），则直接通过调用LockSupport.park(this)来阻塞当前线程的执行。
 * 当获取锁的线程执行完后，调用unlock方法将锁变量修改为false,并解除队首线程的阻塞状态。位于队首的线程判断自己是不是队首元素，如果是了就修改原子变量的值来上锁。
 */
public class FIFOMutex {
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<Thread>();

    public void lock() {
        boolean wasInterrupted = false;
        Thread current = Thread.currentThread();
        waiters.add(current);

        // Block while not first in queue or cannot acquire lock
        while (waiters.peek() != current || !locked.compareAndSet(false, true)) {
            /**
             * 该方法使得当前线程不能参与线程调度，除非当前线程获取许可。
             * 如果获取了许可并消费，则该方法调用就立刻返回了。
             * 有三种情况可以是该方法调用返回。第一种情况是调用LockSupport.unpark方法并将该线程作为参数传入。第二种是其它线程中断了该线程。第三种情况是该方法(park)虚假返回。
             *
             * 那park方法纠结是怎样阻塞当前线程的呢？继续深入
             *
             * setBlocker方法将给当前线程的字段parkBlocker的值修改为方法参数指定的值
             * 调用UNSAFE.park方法
             * setBlocker方法将给当前线程的字段parkBlocker的值修改为null
             * 我们知道setBlocker并没有导致当前线程阻塞，那只能是UNSAFE.park方法导致的。
             * Unsafe类的具体作用可以参考我以前的文章。当然了，也可以参考这篇:Understanding sun.misc.Unsafe(https://dzone.com/articles/understanding-sunmiscunsafe)和http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/9b8c96f96a0f/src/share/classes/sun/misc/Unsafe.java
             */
            LockSupport.park(this);
            if (Thread.interrupted()) // ignore interrupts while waiting
                wasInterrupted = true;
        }

        waiters.remove();
        if (wasInterrupted)  // reassert interrupt status on     exit
            current.interrupt();
    }

    public void unlock() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }
}
