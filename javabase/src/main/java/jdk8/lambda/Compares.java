package jdk8.lambda;

import java.util.Comparator;

/**
 * @author muqi.lmq
 * @date 2018/6/12.
 */
public class Compares {
    /**
     * 多字段比较
     *
     * @return
     */
    public static Comparator<StreamPartitions.Domain> compare() {
        Comparator<StreamPartitions.Domain> comparator = Comparator.comparing((StreamPartitions.Domain e) -> e.getA())
                .thenComparing(e -> e.getB());
        return comparator;
    }
}
