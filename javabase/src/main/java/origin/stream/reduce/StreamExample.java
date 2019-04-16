package origin.stream.reduce;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author muqi.lmq
 * @date 08/03/2018.
 */
public class StreamExample {
    static ExecutorService executor = Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 100));

    public static void main(String[] args) {
        completableFutureTest();
        reduceCustom();
    }

    public static void completableFutureTest() {
//        T(线程数) = N(服务器内核数) * u(期望cpu利用率) * （1 + E(等待时间)/C(计算时间));
        // supplyAsync需要有返回值，runAsync不需要有返回值
        List<Long> list = LongStream.rangeClosed(1, 200).boxed().collect(Collectors.toList());

//        runAsync：异步执行没有返回值；
//        supplyAsync：异步执行有返回值；
//        thenApply：继续执行当前线程future完成的函数，不需要阻塞等待其处理完成；
//        thenApplyAsync：在不同线程池异步地应用参数中的函数；
//        thenCompose：用于多个彼此依赖的futrue进行串联起来
//        thenCombine：并联起两个独立的future，注意，这些future都是在长时间计算都完成以后
        List<Long> sleep_wake_up = list.stream().map(a -> CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10 * a);
                System.out.println("sleep wake up");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return a;
        }, executor)).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("====================");
        System.out.println(sleep_wake_up.size());
        System.out.println("====================");

    }

    public static void reduceCustom() {
        //List<String> adcodes = new ArrayList<String>();
        //Set<String> adcodes_map = new HashSet<String>();
        //String optional = adcodes.origin.stream().filter(a -> !adcodes_map.contains(a)).map(d -> {
        //    System.out.println("#################");
        //    System.out.println(d);
        //    System.out.println("#################");
        //    return String.valueOf(d);
        //}).reduce("",
        //    (j, p) -> {
        //        if (NumberUtils.isNumber(j)) {
        //            j = j;
        //        }
        //        if (NumberUtils.isNumber(p)) {
        //            p = p;
        //        }
        //        System.out.format("accumulator: sum=%s; person=%s\n", j.toString(), p);
        //        return j + p;
        //    },
        //    (j1, j2) -> {
        //        System.out.format("combiner: sum1=%s; sum2=%s\n", j1.toString(), j2.toString());
        //        return j1 + j2;
        //    }
        //);
        String reduce = Stream.of("d2", "a2", "b1", "b3", "c")
                .parallel()
                .filter(c -> c.startsWith("b"))
                .reduce(new StreamExample().toString(),
                        (u, t) -> {
                            System.out.format("accumulator: u=%s; t=%s\n", u, t);
                            if (t.contains("1")) {
                                u = new StreamExample().toString();
                            }
                            return u + "@@@@" + t;
                        },
                        (u1, u2) -> {
                            System.out.format("combiner: u1=%s; u2=%s\n", u1, u2);
                            return u1 + "####" + u2;
                        });

        System.out.println("==========================");
        System.out.println(reduce.toString());
        System.out.println("==========================");

        //        Collector.of(
        //                () -> new StringJoiner(" | "),          // supplier
        //                (j, p) -> j.add(p.name.toUpperCase()),  // accumulator
        //                (j1, j2) -> j1.merge(j2),               // combiner
        //                StringJoiner::toString);                // finisher

        //Collector<String, StringJoiner, String> stringCollector = Collector.of(
        //    () -> new StringJoiner("|"),
        //    (j, p) -> {
        //        System.out.format("accumulator: sum=%s; person=%s\n", j.toString(), p);
        //        j.add(p.toString());
        //    },
        //    (j1, j2) -> {
        //        System.out.format("combiner: sum1=%s; sum2=%s\n", j1.toString(), j2.toString());
        //        return j1.merge(j2);
        //    }, StringJoiner::toString
        //);
        //reduce = Stream.of("d2", "a2", "b1", "b3", "c").parallel()
        //               .collect(stringCollector);
        //System.out.println(reduce.toString());
    }
}
