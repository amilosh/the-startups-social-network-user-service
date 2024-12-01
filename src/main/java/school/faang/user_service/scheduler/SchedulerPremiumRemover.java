package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerPremiumRemover {
    private final PremiumService premiumService;

    @Scheduled(cron = "${myapp.schedule.cron}")
    public void deleteExpiredPremiums() {
        log.info("Scheduled task: deleting premium access that has expired");
        String taskId = "DeleteExpiredPremiums-" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        long startTime = System.currentTimeMillis();
        try {
            premiumService.deleteExpiredPremiums(now);
            log.info("TaskId {} scheduled task completed in {} ms.",taskId, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("TaskId {} Scheduled task failed: {}",taskId, e.getMessage(), e);
        }
    }
}
