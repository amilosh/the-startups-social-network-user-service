package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private long followerId = 1;
    private long followeeId = 2;

    @Test
    void followUser_shouldCallValidatorAndRepository() {
        assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));

        verify(subscriptionValidator).validateFollowUser(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUser_shouldCallValidatorAndRepository() {
        assertDoesNotThrow(() -> subscriptionService.unfollowUser(followerId, followeeId));

        verify(subscriptionValidator).validateUnfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowersCount_shouldReturnCount() {
        long followeeId = 1;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(5);
        long count = assertDoesNotThrow(() -> subscriptionService.getFollowersCount(followeeId));

        assertEquals(5, count);

        verify(subscriptionValidator).validateUserExists(followeeId);
    }

    @Test
    void getFollowingCount_shouldReturnCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(3);
        long count = assertDoesNotThrow(() -> subscriptionService.getFollowingCount(followerId));

        assertEquals(3, count);

        verify(subscriptionValidator).validateUserExists(followerId);
    }
}