package jdk8.lambda;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * @author muqi.lmq
 * @date 19/03/2018.
 */
@FunctionalInterface
public interface MyFunc<T> {
    void accept(T t);

    default boolean tryAdvance(IntConsumer action) {
        System.out.println(action instanceof IntConsumer);
        action.accept(100);
        return false;
    }

    default boolean tryAdvance(Consumer<? super Integer> action) {
        if (action instanceof IntConsumer) {
            return tryAdvance((IntConsumer)action);
        } else {
            //if (Tripwire.ENABLED) {
            //    Tripwire.trip(getClass(),
            //        "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
            //}
            return tryAdvance((IntConsumer)action::accept);
        }
    }

}
