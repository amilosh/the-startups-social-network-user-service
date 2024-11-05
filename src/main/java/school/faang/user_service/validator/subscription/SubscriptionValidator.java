package school.faang.user_service.validator.subscription;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.recommendation.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@Data
public class SubscriptionValidator {
    private final SubscriptionRepository subscriptionRepository;
    private final UserFilter userFilter;

    public void validateUserIsTryingToCallHimself(long followerId, long followeeId, String messageForException) {
        if (followerId == followeeId) {
            throw new DataValidationException(messageForException);
        }
    }

    public void validateUserAlreadyHasThisSubscription(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User " + followeeId + " already has such a subscription");
        }
    }

    public void validateUserFilterIsApplicable(UserFilterDto filter) {
        if (!userFilter.isApplicable(filter)) {
            throw new DataValidationException("""
                    Filter not applicable. One of the following variables must not be null:
                    - namePattern
                    - emailPattern
                    - phonePattern
                    """
            );
        }
    }
}
