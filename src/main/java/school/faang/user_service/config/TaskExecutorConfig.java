package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class TaskExecutorConfig {
    @Value("${app.async-config.task-executor.core-pool-size}")
    private int corePoolSize;

    @Value("${app.async-config.task-executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.async-config.task-executor.queue-capacity}")
    private int queueCapacity;

    @Value("${app.async-config.task-executor.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
