package origin.concurrency;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author muqi.lmq
 * @date 2018/7/10.
 */
public class TurnPrintByThread {
    private static final Unsafe U;
    private static final long SELFCOUNTER_ADDR;
    static int num = 0;
    static volatile int turn = 0;

    static {
        try {
            U = getUnsafe();
            Class<?> k = TurnPrintByThread.IdContain.class;
            SELFCOUNTER_ADDR = U.objectFieldOffset(k.getDeclaredField("i"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static void main(String[] args) {
//        originTurnPrint();
        mTurnPrint();
    }

    private static String who(int i) {
        switch (i) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
        }
        return i + "";
    }

    private static int turnWho(int i) {
        switch (i) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 0;
        }
        return 0;
    }

    public static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
        }
        return null;
    }

    //wrong!!!!!!!!!!!
    private static void mTurnPrint() {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            IdContain id = new IdContain(i);
            Thread t0 = new Thread(() -> {
                for (; 100 > IdContain.num; ) {
                    if ((turn == id.get()) && (IdContain.num == 0 || ++IdContain.num % 3 == id.get())) {
                        System.out.println(who(id.get()) + "   id:" + id.get() + "   " + ":" + IdContain.num);
                        turn = turnWho(id.get());
                    }
                }
            }
            );
            ts.add(t0);
        }
        ts.stream().forEach(t -> t.start());
    }

    private static void originTurnPrint() {
        Thread t0 = new Thread(() -> {
            for (; 100 > num; ) {
                if ((turn == 0) && (num == 0 || ++num % 3 == 0)) {
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

    static class IdContain {
        static int num = 0;
        volatile int i;

        public IdContain(int i) {
            this.i = i;
        }

        public int get() {
            return U.getInt(this, SELFCOUNTER_ADDR);
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}
