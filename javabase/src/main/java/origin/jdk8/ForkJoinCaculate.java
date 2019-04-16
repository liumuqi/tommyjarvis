package origin.jdk8;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinCaculate {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            cal();
        }
//        Instant now1 = Instant.now();
//        long sum = 0;
//        for (int i = 0; i < 900000000; i++) {
//            sum += i;
//        }
//        Instant end1 = Instant.now();
//        System.out.println(Duration.between(now1, end1).toMillis());
    }

    private static void cal() {
        Instant now = Instant.now();
        ForkJoinPool fjp = new ForkJoinPool();
        ForkJoinTask<Long> fjt = new ForkJoinCaculate0(1, 900000000);
        Long invoke = fjp.invoke(fjt);
        Instant end = Instant.now();
        System.out.println(invoke+"--"+Duration.between(now, end).toMillis());
    }

    static class ForkJoinCaculate0 extends RecursiveTask<Long> {
        private long start;
        private long end;
        private int threshold = 1000;

        public ForkJoinCaculate0(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long length = end - start;
            if (length < threshold) {
                long sum = 0;
                for (long i = start; i < end; i++) {
                    sum += i;
                }
                return sum;
            } else {
                long middle = (start + end) / 2;
                ForkJoinCaculate0 fc_left = new ForkJoinCaculate0(start, middle);
                ForkJoinCaculate0 fc_right = new ForkJoinCaculate0(middle + 1, end);
//                fc_left.fork(); // 会比invokeall慢
//                fc_right.fork();
                invokeAll(fc_left, fc_right);
                return fc_left.join() + fc_right.join();
            }
        }
//        @Override
//        protected Long compute() {
//            long length = end - start;
//            List<RecursiveTask<Long>> taskList = new ArrayList<>();
//            if (length < threshold) {
//                long sum = 0;
//                for (long i = start; i < end; i++) {
//                    sum += i;
//                }
//                return sum;
//            } else {
//                while (start < end) {
//                    if (start + threshold < end) {
//                        taskList.add(new ForkJoinCaculate0_sub(start, start + threshold));
//                        start += threshold;
//                    } else {
//                        taskList.add(new ForkJoinCaculate0_sub(start, end));
//                        start += threshold;
//                    }
//                }
//            }
//            Collection<RecursiveTask<Long>> recursiveTasks = invokeAll(taskList);
//            long num = 0;
//            for (RecursiveTask<Long> task : recursiveTasks) {
//                Long subNum = task.join();
//                if (subNum != null) {
//                    num += subNum;
//                }
//            }
//            return num;
//        }
    }

    static class ForkJoinCaculate0_sub extends RecursiveTask<Long> {
        private long start;
        private long end;

        public ForkJoinCaculate0_sub(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long sum = 0;
            for (long i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        }
    }
}
