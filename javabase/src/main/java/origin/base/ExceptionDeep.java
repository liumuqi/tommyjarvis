package origin.base;
/**
 * @author muqi.lmq
 * @date 2018/6/1.
 */

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.concurrent.*;

public class ExceptionDeep {

    public static void main(String[] args) throws InterruptedException {
        // 设置未捕获异常处理器，这里是默认的，当然你可以自己新建一个类，然后实现UncaughtExceptionHandler接口即可
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> System.err.println(t.getName() + "_" + t.getThreadGroup() + "程序抛出了一个异常，异常类型为 ： " + ExceptionUtils.getFullStackTrace(e)));
        Thread thread = new Thread(new Task());
        thread.setUncaughtExceptionHandler((t, e) -> System.out.println(t.getThreadGroup() + ":###############:" + ExceptionUtils.getFullStackTrace(e)));
        thread.start();
//        Map<String, String> map = null;
//        System.err.println(map.get("name"));
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        ThreadFactory threadFactory = builder.setNameFormat("task-%d")
//                .setUncaughtExceptionHandler((t, e) -> { System.out.println(t.getThreadGroup() + "__" + t.getName() + ExceptionUtils.getFullStackTrace(e));})
                .build();
        threadFactory.newThread(new Task()).start();
        threadFactory.newThread(new Task()).start();
        threadFactory.newThread(new Task()).start();

        Future<Object> submit = Executors.newSingleThreadExecutor(threadFactory)
                .submit(() -> {
                    throw new RuntimeException("testxxxxxxxxxxxxxxxxxxxxxxxxx");
                });
        TimeUnit.SECONDS.sleep(5L);

        try {
            Object o = submit.get();
        } catch (ExecutionException e) {
            System.out.println("====================");
        }

        TimeUnit.MINUTES.sleep(5L);
    }
}

class Task implements Runnable {

    @Override
    public void run() {
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            System.out.println("vvvvvvvvv");
            throw new NullPointerException("ccc");
        }
    }
}
