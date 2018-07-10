package spring;

import com.offbytwo.jenkins.JenkinsServer;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import spring.websocket.SocketProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author muqi.lmq
 * @date 2018/7/3.
 */

@org.springframework.context.annotation.Configuration
@EnableScheduling
@EnableAsync
@EnableWebSocket
@AutoConfigureAfter(Configuration.class)
public class Configuration {

    public class BaseProcessConfiguration implements AsyncConfigurer, SchedulingConfigurer, WebSocketConfigurer {
//        @Autowired
//        private BaseConfig baseConfig;

//    @Bean
//    public MethodValidationInterceptor MethodValidationInterceptor() {
//        return new MethodValidationInterceptor();
//    }

        @Bean(name = "baseAsyncExe")
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
            taskExecutor.setCorePoolSize(16);
            taskExecutor.setQueueCapacity(-1);
            taskExecutor.setMaxPoolSize(32);
            taskExecutor.setThreadNamePrefix("Base-Async");
            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            taskExecutor.initialize();
            return taskExecutor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (ex, method, params) -> System.err.printf(String.format("[system]Unexpected error occurred invoking async method '%s'. error:{}", method), ex);
        }


        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setScheduler(taskExecutor());
        }

        public Executor taskExecutor() {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
            return scheduledExecutorService;
        }


        @Bean
        public JenkinsServer getJenkinsServer() {
            try {
                return new JenkinsServer(new URI("url"), "", "");
            } catch (URISyntaxException e) {
            }
            return null;
        }

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(new SocketProcessor(), " /jobstatus").setAllowedOrigins("*");
        }

    }
}
