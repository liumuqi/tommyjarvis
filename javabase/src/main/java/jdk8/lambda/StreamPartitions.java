package jdk8.lambda;

import utils.DateUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author muqi.lmq
 * @date 2018/6/12.
 */
public class StreamPartitions {
    private static final int PARTITION_SIZE = 100;
    private static final int BETWEENTIME = 20 * 60;//20180101195000

    public static Comparator<Domain> compare() {
        Comparator<Domain> comparator = Comparator.comparing((Domain e) -> e.getA())
                .thenComparing(e -> e.getB());
        return comparator;
    }

    public static Double ratio(String PARTION_SOLVE, List<Domain> rtps, List<Integer> adcodes,
                               List<Integer> hours, List<Integer> ranks) {
        HashSet<List<Domain>> partitions = partitions(rtps, adcodes, hours, ranks);
        Map<String, Long> map = groupbyState(partitions);
        Integer total = map.values().parallelStream().collect(Collectors.summingInt(d -> d.intValue()));
        Long partCount = map.get(PARTION_SOLVE);
        partCount = partCount == null ? 0 : partCount;
        if (total == null || total == 0) {
            return 0D;
        }
        return partCount * 1.0 * 100 / total;
    }

    private static Map<String, Long> groupbyState(HashSet<List<Domain>> groupSet) {
        return groupSet.parallelStream().collect(Collectors.groupingBy(d -> {
            List<Domain> domains = d.parallelStream()
                    .filter(e -> !e.getA().equals(e.getB()))
                    .collect(Collectors.toList());
            String state = domains.size() == 0 ? "allsolve" : (domains.size() < d.size() ? "partsolve" : "nonesolve");
            return state;
        }, Collectors.counting()));
    }

    public static HashSet<List<Domain>> partitions(List<Domain> rtps, List<Integer> adcodes,
                                                   List<Integer> hours, List<Integer> ranks) {
        Stream<Domain> congestionDomainStream = StreamSupport.stream(
                new DomainSpliterator(rtps, 0, rtps.size()), true);
        ArrayList<List<Domain>> groups = congestionDomainStream
                .sorted(compare())
//                .filter(getExtremeCnPredicate(adcodes, hours, ranks))
                .sequential().reduce(new ArrayList<>(),
                        (u, t) -> {
                            int size = u.size();
                            if (size == 0 && t != null) {
                                ArrayList<Domain> congestionDomains = new ArrayList<>();
                                congestionDomains.add(t);
                                u.add(congestionDomains);
                            } else if (t != null) {
                                List<Domain> list = u.get(size - 1);
                                int all = list.size();
                                if (all == 0) {
                                    list.add(t);
                                } else {
                                    Domain before = list.get(all - 1);
                                    long fromTime = DateUtils.getUnixTimestampFromTime(before.getB().toString(), DateUtils.FORMAT_yyyyMMddHHmmss);
                                    long timestamp = DateUtils.getUnixTimestampFromTime(t.getB().toString(), DateUtils.FORMAT_yyyyMMddHHmmss);
                                    boolean curr_group = t.getA().equals(before.getA()) && (timestamp - fromTime) < BETWEENTIME;
                                    if (curr_group) {
                                        list.add(t);
                                    } else {
                                        ArrayList<Domain> domains = new ArrayList<>();
                                        domains.add(t);
                                        u.add(domains);
                                    }
                                }
                            }
                            return u;
                        },
                        (u1, u2) -> {
                            ArrayList<List<Domain>> objects = new ArrayList<>();
                            if (u1 != null) {
                                objects.addAll(u1);
                            }
                            if (u2 != null) {
                                objects.addAll(u2);
                            }
                            return objects;
                        });
        HashSet<List<Domain>> ecs = new HashSet<>();
        ecs.addAll(groups);
        return ecs;
    }

    /**
     * 极度拥堵spliterator,其中list是 a,batchTime排序的
     */
    static class DomainSpliterator implements Spliterator<Domain> {
        private final List<Domain> lists;
        private final int endIndex;
        private int index = 0;

        public DomainSpliterator(List<Domain> lists, int index, int endIndex) {
            this.lists = lists;
            this.index = index;
            this.endIndex = endIndex;
        }

        @Override
        public void forEachRemaining(Consumer<? super Domain> action) {
            for (; index < endIndex; index++)
                action.accept(lists.get(index));
        }

        @Override
        public boolean tryAdvance(Consumer<? super Domain> action) {
            if (index < endIndex) {
                action.accept(lists.get(index++));
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<Domain> trySplit() {
            if ((endIndex - index) <= PARTITION_SIZE) {
//                System.out.printf("smalldata[%s]\n",lists.subList(index,endIndex));
                return null;
            }
            int mid = (index + endIndex) >>> 1;
            int lo = index;
            for (int pos = mid; pos >= 1 && lo < pos && pos < endIndex; pos++) {
                int afterIndex = pos;
                Domain currDomain = lists.get(pos - 1);
                Domain afterDomain = lists.get(afterIndex);
                Long linkId = currDomain.getA();
                Long afterLink = afterDomain.getA();
                boolean equalLink = linkId.equals(afterLink);
                long fromTime = DateUtils.getUnixTimestampFromTime(currDomain.getB().toString(), DateUtils.FORMAT_yyyyMMddHHmmss);
                long timestamp = DateUtils.getUnixTimestampFromTime(afterDomain.getB().toString(), DateUtils.FORMAT_yyyyMMddHHmmss);
                long diffTime = timestamp - fromTime;
                boolean b = equalLink && diffTime < BETWEENTIME;
                if (!b) {
                    index = pos;
//                    System.out.printf("data[%s]\n",lists.subList(lo,pos));
                    DomainSpliterator extremeCongetsionSpliterator = new DomainSpliterator(lists, lo, pos);
                    return extremeCongetsionSpliterator;
                }
            }
            return null;
        }

        @Override
        public long estimateSize() {
            return endIndex - index;
        }

        @Override
        public int characteristics() {
            return ORDERED | SIZED | SUBSIZED | NONNULL | CONCURRENT;
        }
    }

    class Domain {
        private Long a;
        private Long b;

        public Long getA() {
            return a;
        }

        public void setA(Long a) {
            this.a = a;
        }

        public Long getB() {
            return b;
        }

        public void setB(Long b) {
            this.b = b;
        }
    }
}
