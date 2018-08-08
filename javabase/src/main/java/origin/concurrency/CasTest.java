package origin.concurrency;

import java.lang.invoke.MethodHandles;
//import java.lang.invoke.VarHandle;

/**
 * @author muqi.lmq
 * @date 2018/7/27.
 */
public class CasTest {
    private static int value;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        CasTest casTest = new CasTest();
        casTest.invoke();
    }

    public void invoke() throws NoSuchFieldException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
//        VarHandle varHandle = lookup.findVarHandle(CasTest.class, "value", int.class);
//        value = 0;
//        boolean result = varHandle.compareAndSet(CasTest.this, 1, 2);
//        System.out.println("compareAndSet, expected: " + 1 +
//                ", x: " + 2 + ", result: " + result);
//        result = varHandle.compareAndSet(CasTest.this, 0, 1);
//        System.out.println("compareAndSet, expected: " + 0 +
//                ", x: " + 1 + ", result: " + result);
    }
}
