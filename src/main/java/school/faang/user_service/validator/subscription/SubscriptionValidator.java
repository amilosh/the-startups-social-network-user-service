package school.faang.user_service.validator.subscription;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.SubscriptionRepository;

@Data
@Component
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;
    private final UserFilter userFilter;

    public void validateUserIsTryingToCallHimself(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("User " + followeeId + " tries to perform an action on himself");
        }
    }

    public void validateUserAlreadyHasThisSubscription(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User " + followeeId + " already has such a subscription");
        }
    }
}
