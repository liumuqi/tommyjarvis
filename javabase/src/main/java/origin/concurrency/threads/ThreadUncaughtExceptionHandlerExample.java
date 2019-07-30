package origin.concurrency.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author:qishan
 * @Date: 2019-07-16
 * @Desc:
 **/
public class ThreadUncaughtExceptionHandlerExample {

    public static void executors() throws InterruptedException {
        CaughtExceptionsThreadFactory threadFactory = new CaughtExceptionsThreadFactory();
        ExecutorService executor = Executors.newCachedThreadPool(threadFactory);

        System.out.println("Executing tasks:");
        for (int i = 0; i < 5; ++i) {
            executor.execute(new ExceptionalTask());
        }

        System.out.println("Shutting down the executor.");
        // we'll wait for two seconds, just to have clean output
        executor.awaitTermination(2, TimeUnit.SECONDS);

        printResults(threadFactory.handler);
    }

    private static void printResults(MyUncaughtExceptionHandler handler) {
        System.out.println("\nCaught errors:");
        handler.errors.forEach(System.out::println);
    }

    public static void main(String[] args) {
        MyUncaughtExceptionHandler handler = new MyUncaughtExceptionHandler();
        startThreadsWithPerThreadHandler(handler);
        printResults(handler);

        System.out.println("\nClearing errors in handler.\n");
        handler.errors.clear();

        startThreadsWithDefaultHandler(handler);
        printResults(handler);


        ExceptionalThread command = new ExceptionalThread();
        command.start();
        System.out.println("\nClearing errors in handler.\n");
        handler.errors.clear();

    }

    private static void startThreadsWithDefaultHandler(MyUncaughtExceptionHandler handler) {
        System.out.println("Setting default uncaught exception handler.");
        Thread.setDefaultUncaughtExceptionHandler(handler);
        System.out.println("Starting threads");
        for (int i = 0; i < 5; ++i) {
            new ExceptionalThread().start();
        }
    }

    private static void startThreadsWithPerThreadHandler(MyUncaughtExceptionHandler handler) {
        System.out.println("Starting threads with per thread uncaught exception handler");
        for (int i = 0; i < 5; ++i) {
            ExceptionalThread command = new ExceptionalThread();
            command.setUncaughtExceptionHandler(handler);
            command.start();
        }
    }



    public static class ExceptionalTask implements Runnable {
        private static int ntasks = 0;
        private int taskId = ++ntasks;

        @Override
        public void run() {
            System.out.println("Starting work in thread: " + taskId);
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                //ignore
            }
            if ((taskId % 2) == 0) {
                throw new RuntimeException("An Exception that ends task: " + taskId);
            } else {
                System.out.printf("Task %d finished normally.%n", taskId);
            }
        }
    }

    private static class CaughtExceptionsThreadFactory implements ThreadFactory {
        private MyUncaughtExceptionHandler handler = new MyUncaughtExceptionHandler();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler(handler);
            return t;
        }
    }

    public static class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        private List<String> errors = new LinkedList<>();

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            String message = String.format("Thread %s hit by exception %s.", t.getName(), e.toString());
            System.out.println(message);
            errors.add(message);
        }
    }

    private static class ExceptionalThread extends Thread {
        @Override
        public void run() {
            System.out.println("Starting work in thread: " + getId());
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                //ignore
            }
            // throw an exception when thread ID is even:
            if ((getId() % 2) == 0) {
                throw new RuntimeException("An Exception that ends the thread: " + getId());
            } else {
                System.out.printf("Thread %d finished normally.%n", getId());
            }
        }
    }
}
