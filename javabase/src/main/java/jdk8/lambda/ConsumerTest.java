package jdk8.lambda;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author muqi.lmq
 * @date 19/03/2018.
 */
public class ConsumerTest {
    @Autowired
    private ResourceLoader resourceLoader;

    public static void main(String[] args) {
        ConsumerTest consumerTest = new ConsumerTest();
        Consumer<Integer> consumer = i -> System.out.println(i);
        IntConsumer intConsumer = i -> System.out.println(i);
        consumerTest.test(consumer);  // 面向对象的方式
        consumerTest.test(consumer::accept); // 函数式的方式
        consumerTest.test(intConsumer::accept); // 函数式的方式

    }

    public void test() throws IOException {
        Resource resource = resourceLoader.getResource("path");
        File cf = resource.getFile();
        List<String> strings = Files.readAllLines(cf.toPath());
        Map<Integer, String> collect = strings.stream().map(s -> {
            String[] kv = StringUtils.split(s, ",");
            Map.Entry<Integer, String> entry = new AbstractMap.SimpleEntry<>(Integer.valueOf(kv[0]), kv[1]);
            return entry;
        }).flatMap(Stream::of).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //key可以是list
        Map<List<Integer>, Long> dataMap = collect.keySet().parallelStream().map(d -> {
            return null;
        }).collect(Collectors.groupingBy(b -> Arrays.asList(1, 2, 3), Collectors.counting()));
    }

    public void test(Consumer<? super Integer> consumer) {
        System.out.println(consumer instanceof IntConsumer);
        consumer.accept(100);
    }
}
