package stream.reduce;

import java.util.stream.Stream;

/**
 * @author muqi.lmq
 * @date 08/03/2018.
 */
public class StreamExample {
    public static void main(String[] args) {
        reduceCustom();
    }

    public static void reduceCustom() {
        //List<String> adcodes = new ArrayList<String>();
        //Set<String> adcodes_map = new HashSet<String>();
        //String optional = adcodes.stream().filter(a -> !adcodes_map.contains(a)).map(d -> {
        //    System.out.println("#################");
        //    System.out.println(d);
        //    System.out.println("#################");
        //    return String.valueOf(d);
        //}).reduce("",
        //    (j, p) -> {
        //        if (NumberUtils.isNumber(j)) {
        //            j = j;
        //        }
        //        if (NumberUtils.isNumber(p)) {
        //            p = p;
        //        }
        //        System.out.format("accumulator: sum=%s; person=%s\n", j.toString(), p);
        //        return j + p;
        //    },
        //    (j1, j2) -> {
        //        System.out.format("combiner: sum1=%s; sum2=%s\n", j1.toString(), j2.toString());
        //        return j1 + j2;
        //    }
        //);
        String reduce = Stream.of("d2", "a2", "b1", "b3", "c")
                              .parallel()
                              .filter(c -> c.startsWith("b"))
                              .reduce(new StreamExample().toString(),
                                  (u, t) -> {
                                      System.out.format("accumulator: u=%s; t=%s\n", u, t);
                                      if (t.contains("1")) {u = new StreamExample().toString();}
                                      return u + "@@@@" + t;
                                  },
                                  (u1, u2) -> {
                                      System.out.format("combiner: u1=%s; u2=%s\n", u1, u2);
                                      return u1 + "####" + u2;
                                  });

        System.out.println("==========================");
        System.out.println(reduce.toString());
        System.out.println("==========================");

        //        Collector.of(
        //                () -> new StringJoiner(" | "),          // supplier
        //                (j, p) -> j.add(p.name.toUpperCase()),  // accumulator
        //                (j1, j2) -> j1.merge(j2),               // combiner
        //                StringJoiner::toString);                // finisher

        //Collector<String, StringJoiner, String> stringCollector = Collector.of(
        //    () -> new StringJoiner("|"),
        //    (j, p) -> {
        //        System.out.format("accumulator: sum=%s; person=%s\n", j.toString(), p);
        //        j.add(p.toString());
        //    },
        //    (j1, j2) -> {
        //        System.out.format("combiner: sum1=%s; sum2=%s\n", j1.toString(), j2.toString());
        //        return j1.merge(j2);
        //    }, StringJoiner::toString
        //);
        //reduce = Stream.of("d2", "a2", "b1", "b3", "c").parallel()
        //               .collect(stringCollector);
        //System.out.println(reduce.toString());
    }
}
