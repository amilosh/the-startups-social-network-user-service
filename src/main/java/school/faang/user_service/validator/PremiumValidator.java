package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumValidator {
    private final PremiumRepository repository;

    public void validateUserIsNotPremium(long userId) {
        if (doesUserHavePremium(userId)) {
            log.warn("Validation failed: User with userId {} already has premium access", userId);
            throw new IllegalStateException("User with userId: " + userId + " already has premium");
        }
    }

    private boolean doesUserHavePremium(long userId) {
        return repository.existsByUserId(userId);
    }
}
