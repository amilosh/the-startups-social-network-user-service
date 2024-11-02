package school.faang.user_service.service.service;

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

    @Test
    void testYourselfFollowing() {
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(1L, 1L));
        verify(subscriptionRepository, times(0)).followUser(1L, 2L);
    }

    @Test
    void testExistsFollowing() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(1L, 2L));
        verify(subscriptionRepository, times(0)).followUser(1L, 2L);
    }

    @Test
    void testNotExistsFollowing() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(anyLong(), anyLong())).thenReturn(false);

        assertDoesNotThrow(() -> subscriptionService.followUser(1L, 2L));
        verify(subscriptionRepository, times(1)).followUser(1L, 2L);
    }

}
