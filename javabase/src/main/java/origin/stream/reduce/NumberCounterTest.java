package origin.stream.reduce;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author muqi.lmq
 * @date 16/03/2018.
 */
public class NumberCounterTest {
    private static int countNum(Stream<Character> stream) {
        NumCounter numCounter = stream.reduce(new NumCounter(0, 0, false), NumCounter::accumulate, NumCounter::combine);
        return numCounter.getSum();
    }

    public static void main(String[] args) {
        int x = 0xff & ~1 ;
        int lo = 7; // divide range in half
        int fence = 10;
        int mid = ((lo + fence) >>> 1) & ~1; // force midpoint to be even
        System.out.println(mid );
        int ii = 1;
        System.out.println(ii++);
        String arr = "12%3 101 21sdas s34d dfsdz45 101  R3 jo34 101 sjkf8 3$1P 213ikflsd fdg55 kfd";
        Stream<Character> stream = IntStream.range(0, arr.length()).mapToObj(arr::charAt);
        Long start = System.nanoTime();
        int i = countNum(stream);
        Long cost = System.nanoTime() - start;
        System.out.println("ordered cost : " + cost);
        System.out.println("ordered total: " + i);

        String arr2 = "12%3 101 21sdas s34d dfsdz45 101  R3 jo34 101 sjkf8 3$1P 213ikflsd fdg55 kfd";
        Spliterator<Character> spliterator = new NumCounterSpliterator(arr2);
        // 传入true表示是并行流
        Stream<Character> parallelStream = StreamSupport.stream(spliterator, true);

        Long start2 = System.nanoTime();
        int i2 = countNum(parallelStream);

        Long cost2 = System.nanoTime() - start2;
        System.out.println("parallel cost : " + cost2);
        System.out.println("parallel total: " + i2);
        System.out.println(cost2 - cost);
    }
    //private static int countNum(Stream<Character> origin.stream){
    //    NumCounter numCounter = origin.stream.reduce(new NumCounter(0, 0, false), NumCounter::accumulate, NumCounter::combine);
    //    return numCounter.getSum();
    //}
}

/**
 * 字符串中的数字分割迭代计算器实现
 */
class NumCounterSpliterator implements Spliterator<Character> {
    private String str;
    private int currentChar = 0;

    public NumCounterSpliterator(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getCurrentChar() {
        return currentChar;
    }

    public void setCurrentChar(int currentChar) {
        this.currentChar = currentChar;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        action.accept(str.charAt(currentChar++));
        return currentChar < str.length();
    }

    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = str.length() - currentChar;
        if (currentSize < 10) { return null; }
        for (int pos = currentSize / 2 + currentSize; pos < str.length(); pos++) {
            if (pos + 1 < str.length()) {
                // 当前Character是数字，且下一个Character不是数字，才需要划分一个新的Spliterator
                if (Character.isDigit(str.charAt(pos)) && !Character.isDigit(str.charAt(pos + 1))) {
                    Spliterator<Character> spliterator = new NumCounterSpliterator(str.substring(currentChar, pos));
                    currentChar = pos;
                    return spliterator;
                }
            } else {
                if (Character.isDigit(str.charAt(pos))) {
                    Spliterator<Character> spliterator = new NumCounterSpliterator(str.substring(currentChar, pos));
                    currentChar = pos;
                    return spliterator;
                }
            }
        }
        return null;
    }

    @Override
    public long estimateSize() {
        return str.length() - currentChar;
    }

    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }
}

class NumCounter {
    private int num;
    private int sum;
    // 是否当前是个完整的数字
    private boolean isWholeNum;

    public NumCounter(int num, int sum, boolean isWholeNum) {
        this.num = num;
        this.sum = sum;
        this.isWholeNum = isWholeNum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isWholeNum() {
        return isWholeNum;
    }

    public void setWholeNum(boolean wholeNum) {
        isWholeNum = wholeNum;
    }

    public NumCounter accumulate(Character c) {
        if (Character.isDigit(c)) {
            if (isWholeNum) {
                this.setSum(sum + num);
                this.setNum(Integer.parseInt("" + c));
                this.setWholeNum(false);
            } else {
                this.setSum(sum);
                this.setNum(Integer.parseInt("" + num + c));
                this.setWholeNum(false);
            }
            return this;
            //return isWholeNum ? new NumCounter(Integer.parseInt("" + c), sum + num, false) : new NumCounter(Integer.parseInt("" + num + c), sum, false);
        } else {
            //return new NumCounter(0, sum + num, true);
            this.setSum(sum + num);
            this.setNum(0);
            this.setWholeNum(true);
            return this;
            //return new NumCounter(0, sum + num, true);
        }
    }

    public NumCounter combine(NumCounter numCounter) {
        this.setSum(this.getSum() + numCounter.getSum());
        this.setNum(numCounter.getNum());
        this.setWholeNum(numCounter.isWholeNum());
        return this;
        //return new NumCounter(numCounter.num, this.getSum() + numCounter.getSum(), numCounter.isWholeNum);
    }

    public int getSum() {
        return sum + num;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}