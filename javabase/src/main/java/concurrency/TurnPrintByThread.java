package concurrency;

/**
 * @author muqi.lmq
 * @date 2018/7/10.
 */
public class TurnPrintByThread {
    static int num = 0;
    static volatile int turn = 0;

    public static void main(String[] args) {
        Thread t0 = new Thread(() -> {
            for (; 100 > num; ) {
                if ((turn == 0) && (num == 0 || ++num % 3== 0)) {
                    System.out.println("A:" + num);
                    turn = 1;
                }
            }
        }
        );
        Thread t1 = new Thread(() -> {
            for (; 100 > num; ) {
                if ((turn == 1) && (++num % 3 == 1)) {
                    System.out.println("B:" + num);
                    turn = 2;
                }
            }
        }
        );
        Thread t2 = new Thread(() -> {
            for (; 100 > num; ) {
                if ((turn == 2) && (++num % 3 == 2)) {
                    System.out.println("C:" + num);
                    turn = 0;
                }
            }
        }
        );
        t1.start();
        t0.start();
        t2.start();
    }
}
