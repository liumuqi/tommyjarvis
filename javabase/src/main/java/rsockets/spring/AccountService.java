package rsockets.spring;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author:qishan
 * @Date: 2019-08-10
 * @Desc:
 **/
public class AccountService {
    public Mono<Account> findById(Integer id) {
        return null;
    }

    public Flux<Account> findAll() {
        return null;
    }

    public Flux<Account> findRSocketRequesterAll() {
        return null;
    }
}
