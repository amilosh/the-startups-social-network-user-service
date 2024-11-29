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

    public void deleteAllPremium(LocalDateTime time) {
        log.info("Starting premium deletion process for premiums before: {}", time);
        List<Premium> premiumsToDelete = premiumRepository.findAllByEndDateBefore(time);
        List<List<Premium>> partitionList = partitionList(premiumsToDelete, batchSize);
        log.info("Found {} premiums to delete, splitting into batches of size {}", premiumsToDelete.size(), batchSize);
        partitionList.forEach(batch->{
            try {
                premiumCleanerService.deletePremium(batch);
                log.info("Successfully deleted a batch of {} premiums.", batch.size());
            } catch (Exception e){
                log.error("Error occurred while deleting a batch of {} premiums: {}", batch.size(), e.getMessage(), e);
            }
        });
        log.info("Premium deletion process completed.");
    }

    private List<List<Premium>> partitionList(List<Premium> premiumsForDelete, int size) {
        List<List<Premium>> batchList = new ArrayList<>();
        for (int i = 0; i < premiumsForDelete.size(); i += size) {
            batchList.add(new ArrayList<>(premiumsForDelete.subList(i, Math.min(premiumsForDelete.size(), i + size))));
        }
        return batchList;
    }
}
