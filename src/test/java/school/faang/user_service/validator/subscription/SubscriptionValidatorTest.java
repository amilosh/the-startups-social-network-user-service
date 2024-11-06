package school.faang.user_service.validator.subscription;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {
    @InjectMocks
    private SubscriptionValidator subscriptionValidator;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Test
    public void testUserSubscribeOrUnfollowToHimself() {
        Assertions.assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateUserIsTryingToCallHimself(3L, 3L)
        );
    }

    @Test
    public void testUserAlreadyHasThisSubscription() {
        long followerId = 3L;
        long followeeId = 4L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        Assertions.assertThrows(
                DataValidationException.class,
                () -> subscriptionValidator.validateUserAlreadyHasThisSubscription(followerId, followeeId)
        );
    }
}
