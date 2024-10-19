package school.faang.user_service.service.premium.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import school.faang.user_service.annotation.publisher.PremiumBoughtEventPublisherRedis;

@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
@Component
public class PremiumBoughtEventPublishJob implements Job {
    private final PremiumBoughtEventPublisherRedis premiumBoughtEventPublisherRedis;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (!premiumBoughtEventPublisherRedis.analyticEventsIsEmpty()) {
            log.info("Publish all premium bought event job execute");
            premiumBoughtEventPublisherRedis.publishAllEvents();
        }
    }
}
