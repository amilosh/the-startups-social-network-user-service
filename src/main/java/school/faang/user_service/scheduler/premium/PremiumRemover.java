package school.faang.user_service.scheduler.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemover {

    @Value("${premium.updater.batch-size}")
    private int updaterBatchSize;
    private final PremiumRepository premiumRepository;

    @Async
    @Transactional
    @Scheduled(cron = "${premium.updater.cron}", zone = "${premium.updater.zone}")
    public void removeExpiredPremiums() {
        log.info("updating premiums are working");
        List<Premium> premiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if(premiums == null) {
            log.info("premium users not found!");
            return;
        }
        List<CompletableFuture<Void>> deletedPremiumsFuture = ListUtils
                .partition(premiums, updaterBatchSize).stream()
                .map(this::deleteProcess)
                .toList();

        CompletableFuture.allOf(deletedPremiumsFuture.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("premiums deleted"));
    }

    public CompletableFuture<Void> deleteProcess(List<Premium> premiums) {
        return CompletableFuture.runAsync(() -> {
            premiumRepository.deleteAllInBatch(premiums);
        });
    }
}
