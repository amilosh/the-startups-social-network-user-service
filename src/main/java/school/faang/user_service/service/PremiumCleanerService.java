package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumCleanerService {
    private final PremiumRepository premiumRepository;

    @Async("taskExecutor")
    public CompletableFuture<List<Premium>> deletePremium(List<Premium> premiums) {
        List<Premium> failedPremiums = new ArrayList<>();
        for (Premium premium : premiums) {
            try {
                premiumRepository.delete(premium);
                log.info("Successfully deleted  premium with id {} ", premium.getId());
            } catch (Exception e) {
                log.error("Failed to delete premium with id {}. Error: {} ", premium.getId(), e.getMessage(), e);
                failedPremiums.add(premium);
            }
        }
        return CompletableFuture.completedFuture(failedPremiums);
    }
}
