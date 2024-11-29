package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumCleanerService {
    private final PremiumRepository premiumRepository;

    @Async("taskExecutor")
    public void deletePremium(List<Premium> premiumList) {
        premiumList.forEach(premium -> premiumRepository.delete(premium));
    }
}
