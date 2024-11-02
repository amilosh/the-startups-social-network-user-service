package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.validation.DataValidationException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private long followerId;
    private long followeeId;

    @BeforeEach
    void setUp() {
        followerId = 1L;
        followeeId = 2L;
    }

    @Test
    void testYourselfSubscription() {
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
        verify(subscriptionRepository, times(0)).followUser(followerId, followerId);
    }

    @Test
    void testExistsSubscription() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
        verify(subscriptionRepository, times(0)).followUser(followerId, followeeId);
    }

    @Test
    void testNotExistsSubscription() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionService.followUser(followerId, followeeId));
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void testYourselfUnsubscription() {
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followerId));
        verify(subscriptionRepository, times(0)).followUser(followerId, followerId);
    }

    @Test
    void testUnsubscription() {
        assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(followerId, followeeId));
        verify(subscriptionRepository, times(0)).followUser(followerId, followeeId);
    }

}
