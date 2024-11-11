package school.faang.user_service.scheduler.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumUpdateScheduler {

    private final PremiumRepository premiumRepository;

    @Scheduled(cron = "0 0-3 0 * * *", zone = "Europe/Moscow")
    void updatePremiumWhichExpired() {
        log.info("update premium scheduler working");
        premiumRepository.deletePremiumWhichDateExpire(LocalDateTime.now());
        log.info("premium repository update, deleted the expired premiums");
    }
}
