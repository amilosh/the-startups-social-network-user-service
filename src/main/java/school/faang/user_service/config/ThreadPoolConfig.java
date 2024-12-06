package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread-pool.size}")
    private int total_threads;

    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(total_threads);
    }
}
