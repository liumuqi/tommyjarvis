package origin.concurrency;

public class ReadWriteLock {
    private int readcount = 0;
    private int writecount = 0;

    public static void main(String[] args) throws InterruptedException {
        ReadWriteLock readWriteLock = new ReadWriteLock();
        for (int i = 0; i < 2; i++) {
            Thread2 thread2 = new Thread2(i, readWriteLock);
            thread2.start();
        }
        for (int i = 0; i < 10; i++) {
            Thread1 thread1 = new Thread1(i, readWriteLock);
            thread1.start();
        }
    }

    public void lockread() throws InterruptedException {
        while (writecount > 0) {
            synchronized (this) {
                wait();
            }
        }
        readcount++;
        //进行读取操作
        System.out.println("读操作");
    }

    public void unlockread() {
        readcount--;
        synchronized (this) {
            notifyAll();
        }
    }

    public void lockwrite() throws InterruptedException {
        while (writecount > 0) {
            synchronized (this) {
                wait();
            }
        }
        //之所以在这里先++，是先占一个坑，避免读操作太多，从而产生写的饥饿等待
        writecount++;
        while (readcount > 0) {
            synchronized (this) {
                wait();
            }
        }
        //进行写入操作
        System.out.println("写操作");
    }

    public void unlockwrite() {
        writecount--;
        synchronized (this) {
            notifyAll();
        }
    }
}

class Thread1 extends Thread {
    public int i;
    public ReadWriteLock readWriteLock;

    public Thread1(int i, ReadWriteLock readWriteLock) {
        this.i = i;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void run() {
        try {
            readWriteLock.lockread();
            Thread.sleep(1000);//模拟耗时
            System.out.println("第" + i + "个读任务");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.unlockread();
        }
    }
}

class Thread2 extends Thread {
    public int i;
    public ReadWriteLock readWriteLock;

    public Thread2(int i, ReadWriteLock readWriteLock) {
        this.i = i;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void run() {
        try {
            readWriteLock.lockwrite();
            Thread.sleep(1000);
            System.out.println("第" + i + "个写任务");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.unlockwrite();
        }
    }
}
