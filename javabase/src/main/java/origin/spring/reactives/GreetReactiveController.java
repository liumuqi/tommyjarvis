package origin.spring.reactives;

import org.reactivestreams.Publisher;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * @Author:qishan
 * @Date: 2019/9/2
 * @Desc:
 **/
@RestController
public class GreetReactiveController {
    @GetMapping("/greetings")
    public Publisher<Greeting> greetingPublisher() {
        Flux<Greeting> greetingFlux = Flux.<Greeting>generate(sink -> sink.next(new Greeting("Hello"))).take(50);
        return greetingFlux;
    }

    @GetMapping(value = "/greetings/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<Greeting> sseGreetings() {
        Flux<Greeting> delayElements = Flux
                .<Greeting>generate(sink -> sink.next(new Greeting("Hello @" + Instant.now().toString())))
                .delayElements(Duration.ofSeconds(1));
        return delayElements;
    }

    @GetMapping(value = "/greetings/events/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Greeting> events() {
        Flux<Greeting> greetingFlux = Flux.fromStream(Stream.generate(() -> new Greeting("Hello @" + Instant.now().toString())));
        Flux<Long> durationFlux = Flux.interval(Duration.ofSeconds(1));
        return Flux.zip(greetingFlux, durationFlux).map(Tuple2::getT1);
    }

    @PostMapping(value = "/test", produces = "application/json")
    public Flux<String> handleRequest(@RequestBody String body) {
        return getData(body);
    }

    /**
     * 目前，@Cacheable不适用Flux（和Reactor一般）。但是关于你的例子，每次调用方法时，你都在创建一个新Flux实例，所以很自然，它从不缓存任何东西。
     * 为了能够缓存结果，您需要将a转换Flux为列表实例，或者只是继续重用一个Flux实例
     * @param body
     * @return
     */
//    @Cacheable("testCache")
    public Flux<String> getData(String body) {
        return WebClient.create().post()
                .uri("http://myurl")
                .body(BodyInserters.fromObject(body))
                .retrieve().bodyToFlux(String.class).cache();
    }

    class Person {
        private int age;
        private String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }
    }
}
