package school.faang.user_service.validator.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;

    public void validateUserIsTryingToCallHimself(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.error("User {} tries to perform an action on himself", followeeId);
            throw new DataValidationException("User " + followeeId + " tries to perform an action on himself");
        }
    }

    public void validateUserAlreadyHasThisSubscription(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("User {} already has such a subscription", followeeId);
            throw new DataValidationException("User " + followeeId + " already has such a subscription");
        }
    }
}
