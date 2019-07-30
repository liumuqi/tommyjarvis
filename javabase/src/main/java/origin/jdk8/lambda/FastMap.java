package origin.jdk8.lambda;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
/**
 * @Author:qishan
 * @Date: 2019-07-01
 * @Desc:
 **/
public class FastMap {
    public static void main(String[] args) {
        BigInteger one = BigInteger.ONE;

        Predicate<BigInteger> divBy3 = number -> {
            System.out.println("divBy3:" + number.toString());
            return Objects.equals(number.remainder(BigInteger.valueOf(3)), BigInteger.ZERO);
        };
        Predicate<BigInteger> divBy5 = number -> {
            System.out.println("divBy5:" + number.toString());
            return Objects.equals(number.remainder(BigInteger.valueOf(5)), BigInteger.ZERO);
        };
        Predicate<BigInteger> divBy3And5 =number->{
            System.out.println("divBy3And5:" + number.toString());
            return Objects.equals(number.remainder(BigInteger.valueOf(3)), BigInteger.ZERO)
                    &&
                    Objects.equals(number.remainder(BigInteger.valueOf(5)), BigInteger.ZERO) ;
        } ;

        Stream<BigInteger> numbers = Stream.iterate(one, el -> el.add(one));

        Stream<String> fizzBuzzStream = numbers
                .map(
                        DataMatcher.matching(divBy3And5, () -> "FizzBuzz").otherwise(
                                DataMatcher.matching(divBy3, () -> "Fizz").otherwise(
                                        DataMatcher.matching(divBy5, () -> "Buzz").otherwise(
                                                BigInteger::toString
                                        )
                                )
                        )
                );

        fizzBuzzStream.limit(100).forEach(System.out::println);
    }
}
interface DataMatcher extends Function<BigInteger, Optional<String>> {

    static DataMatcher matching(Predicate<BigInteger> matcher, Supplier<String> resultMsgSupplier) {
        return number -> matcher.test(number) ? Optional.of(resultMsgSupplier.get()) : Optional.empty();
    }

    default Function<BigInteger, String> otherwise(Function<BigInteger, String> next) {
		 return number ->{
            System.out.println("otherwise:"+number);
            Optional<String> apply = apply(number);
            return apply.orElseGet(() -> next.apply(number));
        } ;
    }
}
