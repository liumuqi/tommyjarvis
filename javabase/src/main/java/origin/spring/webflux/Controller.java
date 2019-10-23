package origin.spring.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author muqi.lmq
 * @date 2018/6/26.
 */
@RestController
public class Controller {
    @Autowired
    CacheManager manager;
    private WebClient client;

    @PostConstruct
    public void setup() {
        client = WebClient.builder()
                .baseUrl("http://myurl")
                .exchangeStrategies(ExchangeStrategies.withDefaults())
                .build();
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("hspad")));
        return cacheManager;
    }

    @PostMapping(value = "/hspad/grahql", produces = "application/json")
    public Mono<ResponseEntity<String>> hspadService(@RequestBody String body) {
        return getHspadQuery(body);
    }

    private Mono<ResponseEntity<String>> getHspadQuery(String body) {
        Mono<ResponseEntity<String>> mono;
        Optional<Cache.ValueWrapper> value = Optional.ofNullable(cacheManager().getCache("hspad").get(body));
        if (value.isPresent()) {
            mono = Mono.just(ResponseEntity.ok(value.get().get().toString()));
        } else {
            mono = client.post()
                    .body(BodyInserters.fromObject(body))
                    .retrieve().bodyToMono(String.class).map(response ->
                    {
// Care blocking operation! (use cacheManager -not found yet- prepared for reactive) cacheManager().getCache("hspad").putIfAbsent(body,response);
                        return ResponseEntity.ok(response);
                    });
        }
        return mono;
    }
}
