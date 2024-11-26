package school.faang.user_service.config.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SchedulerConfig {

    @Value("${premium.updater.thread-pool}")
    private int fixedThreadPool;

    @Bean
    public ExecutorService premiumRemoverThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPool);
    }
}
