package rsockets.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author:qishan
 * @Date: 2019-08-10
 * @Desc:
 **/
@Controller
public class AccountRSocketController {
    @Autowired
    private AccountService accountService;

    @MessageMapping("rsockets.spring.AccountService.findById")
    public Mono<Account> findById(Integer id) {
        return accountService.findById(id);
    }

    @MessageMapping("rsockets.spring.AccountService.findAll")
    public Flux<Account> findAll() {
        return accountService.findRSocketRequesterAll();
    }
}
