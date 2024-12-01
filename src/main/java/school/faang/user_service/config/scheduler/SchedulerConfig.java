package school.faang.user_service.config.scheduler;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SchedulerConfig {

    @Value("${scheduler.batch-size}")
    private int batchSize;

    @Value("${scheduler.thread-pool-size}")
    private int threadPoolSize;

}
