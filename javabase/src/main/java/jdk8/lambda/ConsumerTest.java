package jdk8.lambda;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * @author muqi.lmq
 * @date 19/03/2018.
 */
public class ConsumerTest {

    public static void main(String[] args) {
        ConsumerTest consumerTest = new ConsumerTest();
        Consumer<Integer> consumer = i -> System.out.println(i);
        IntConsumer intConsumer = i -> System.out.println(i);
        consumerTest.test(consumer);  // 面向对象的方式
        consumerTest.test(consumer::accept); // 函数式的方式
        consumerTest.test(intConsumer::accept); // 函数式的方式
    }

    public void test(Consumer<? super Integer> consumer) {
        System.out.println(consumer instanceof IntConsumer);
        consumer.accept(100);
    }
}
