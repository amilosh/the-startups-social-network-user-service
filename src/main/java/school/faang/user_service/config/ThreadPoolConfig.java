package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ThreadPoolConfig {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService taskExecutor (){
        log.info("Thread pool created");
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
