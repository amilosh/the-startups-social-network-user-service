package school.faang.user_service.config.thread_pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfiguration {
    @Value("${threadPool.size}")
    private int poolSize;

    @Bean
    public ExecutorService fixedThreadPool(){
        return Executors.newFixedThreadPool(poolSize);
    }
}
