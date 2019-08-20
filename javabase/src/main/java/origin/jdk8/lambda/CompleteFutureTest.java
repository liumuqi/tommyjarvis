package origin.jdk8.lambda;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author:qishan
 * @Date: 2019-08-09
 * @Desc:
 **/
public class CompleteFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //thenApply和thenApplyAsync的区别
        System.out.println("-------------");
        CompletableFuture<String> supplyAsyncWithSleep = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "supplyAsyncWithSleep Thread Id : " + Thread.currentThread();
        });
        CompletableFuture<String> thenApply = supplyAsyncWithSleep
                .thenApply(name -> name + "------thenApply Thread Id : " + Thread.currentThread());
        CompletableFuture<String> thenApplyAsync = supplyAsyncWithSleep
                .thenApplyAsync(name -> name + "------thenApplyAsync Thread Id : " + Thread.currentThread());
        System.out.println("Main Thread Id: " + Thread.currentThread());
        System.out.println(thenApply.get());
        System.out.println(thenApplyAsync.get());
        System.out.println("-------------No Sleep");
        CompletableFuture<String> supplyAsyncNoSleep = CompletableFuture.supplyAsync(() -> {
            return "supplyAsyncNoSleep Thread Id : " + Thread.currentThread();
        });
        CompletableFuture<String> thenApplyNoSleep = supplyAsyncNoSleep
                .thenApply(name -> name + "------thenApply Thread Id : " + Thread.currentThread());
        CompletableFuture<String> thenApplyAsyncNoSleep = supplyAsyncNoSleep
                .thenApplyAsync(name -> name + "------thenApplyAsync Thread Id : " + Thread.currentThread());
        System.out.println("Main Thread Id: " + Thread.currentThread());
        System.out.println(thenApplyNoSleep.get());
        System.out.println(thenApplyAsyncNoSleep.get());
        System.out.println("============================");
        CompletableFuture<Integer> future
                = CompletableFuture.supplyAsync(() -> 0);

        future.thenApplyAsync(x -> x + 1) // call 1
                .thenApplyAsync(x -> x + 1)
                .thenAccept(x -> System.out.println("async result: " + x));

        future.thenApply(x -> x + 1) // call 2
                .thenApply(x -> x + 1)
                .thenAccept(x -> System.out.println("sync result:" + x));
    }
}
