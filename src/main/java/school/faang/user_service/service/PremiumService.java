package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumCleanerService premiumCleanerService;

    @Value("${del.batch-size}")
    private int batchSize;

    public void deleteExpiredPremiums(LocalDateTime time) {
        log.info("Starting premium deletion process for premiums before: {}", time);
        List<Premium> premiumsToDelete = premiumRepository.findAllByEndDateBefore(time);
        List<List<Premium>> partitions = splitPremiumsIntoBatches(premiumsToDelete, batchSize);
        log.info("Found {} premiums to delete, splitting into batches of size {}", premiumsToDelete.size(), batchSize);
        partitions.forEach(premiumCleanerService::deletePremium);
        log.info("Premium deletion process completed.");
    }

    private List<List<Premium>> splitPremiumsIntoBatches(List<Premium> premiumsToDelete, int size) {
        List<List<Premium>> batches = new ArrayList<>();
        for (int i = 0; i < premiumsToDelete.size(); i += size) {
            batches.add(new ArrayList<>(premiumsToDelete.subList(i, Math.min(premiumsToDelete.size(), i + size))));
        }
        return batches;
    }
}
