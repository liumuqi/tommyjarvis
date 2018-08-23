package origin.base;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class DirectAssignMemory {

    private static final Unsafe U;
    private final static long INT_SIZE_IN_BYTES = 4;

    static {
        try {
            U = getUnsafe();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private final long startIndex;

    public DirectAssignMemory(long size) {
        startIndex = U.allocateMemory(size * INT_SIZE_IN_BYTES);
        U.setMemory(startIndex, size * INT_SIZE_IN_BYTES, (byte) 0);
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

    public void setValue(long index, int value) {
        U.putInt(index(index), value);
    }

    public int getValue(long index) {
        return U.getInt(index(index));
    }

    private long index(long offset) {
        return startIndex + offset * INT_SIZE_IN_BYTES;
    }

    public void destroy() {
        U.freeMemory(startIndex);
    }

    public void testDirectIntArray() throws Exception {
        long maximum = Integer.MAX_VALUE + 1L;
        DirectAssignMemory directIntArray = new DirectAssignMemory(maximum);
        directIntArray.setValue(0L, 10);
        directIntArray.setValue(maximum, 20);
        assertEquals(10, directIntArray.getValue(0L));
        assertEquals(20, directIntArray.getValue(maximum));
        directIntArray.destroy();
    }
}
