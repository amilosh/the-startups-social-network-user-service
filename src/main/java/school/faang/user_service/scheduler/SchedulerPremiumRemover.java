package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SchedulerPremiumRemover {
    private final PremiumService premiumService;

    @Scheduled(cron = "${myapp.schedule.cron}")
    public void deleteExpiredPremiums() {
        String taskId = "DeleteExpiredPremiums-" + System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        long startTime = System.currentTimeMillis();
        try {
            premiumService.deleteExpiredPremiums(now);
        } catch (Exception e) {
            throw new RuntimeException("Scheduled task failed: " + e.getMessage(), e);
        }
    }
}
