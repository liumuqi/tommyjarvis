package origin.jdk8.lambda;

/**
 * @Author:qishan
 * @Date: 2019-03-29
 * @Desc:
 **/
public class Examples {
    public static void main(String[] args) {

    }

    /**
     * jdk12
     */
    public void tee() {
//        Collector<CharSequence, ?, String> joiningCollector = Collectors.joining("-");
//        Collector<String, ?, List<String>> listCollector = Collectors.toList();
//        //returns joined string and individual strings as array
//        Collector<String, ?, String[]> compositeCollector = Collectors.teeing(joiningCollector, listCollector,
//                (joinedString, strings) -> {
//                    ArrayList<String> list = new ArrayList<>(strings);
//                    list.add(joinedString);
//                    String[] array = list.toArray(new String[0]);
//                    return array;
//                });
//
//        String[] strings = Stream.of("Apple", "Banana", "Orange").collect(compositeCollector);
//        System.out.println(Arrays.toString(strings));//[Apple, Banana, Orange, Apple-Banana-Orange]
//        Collector<Integer, ?, Long> summing = Collectors.summingLong(Integer::valueOf);
//        Collector<Integer, ?, Long> counting = Collectors.counting();
//
//        //example list
//        List<Integer> list = List.of(1, 2, 4, 5, 7, 8);
//
//        //collector for  map entry consisting of sum and count
//        Collector<Integer, ?, Map.Entry<Long, Long>> sumAndCountMapEntryCollector =
//                Collectors.teeing(summing, counting, Map::entry);
//        Map.Entry<Long, Long> sumAndCountMap = list.stream().collect(sumAndCountMapEntryCollector);
//        System.out.println(sumAndCountMap);
//
//        //collect sum and count as List
//        Collector<Integer, ?, List<Long>> sumAndCountListCollector =
//                Collectors.teeing(summing, counting, List::of);//(v1, v2) -> List.of(v1, v2)
//        List<Long> sumAndCountArray = list.stream().collect(sumAndCountListCollector);
//        System.out.println(sumAndCountArray);
        //27=6
        //[27, 6]
    }
}
