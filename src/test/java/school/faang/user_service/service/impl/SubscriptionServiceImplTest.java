package school.faang.user_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.extention.DataValidationException;
import school.faang.user_service.extention.ErrorMessage;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private final long testIdUser1 = 1L;
    private final long testIdUser2 = 2L;

    @Test
    void followUserPositive() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(testIdUser1, testIdUser2)).thenReturn(false);
        subscriptionService.followUser(testIdUser1, testIdUser2);
        verify(subscriptionRepository, times(1)).followUser(testIdUser1, testIdUser2);
    }

    @Test
    void followUserWhenExistsByFollowerIdAndFolloweeId() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(testIdUser1, testIdUser2)).thenReturn(true);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(testIdUser1, testIdUser2));
        assertEquals(exception.getMessage(), ErrorMessage.M_FOLLOW_EXIST);
        verify(subscriptionRepository, never()).followUser(testIdUser1, testIdUser2);
    }

    @Test
    void unfollowUserPositive() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(testIdUser1, testIdUser2)).thenReturn(true);
        subscriptionService.unfollowUser(testIdUser1, testIdUser2);
        verify(subscriptionRepository, times(1)).unfollowUser(testIdUser1, testIdUser2);
    }

    @Test
    void unfollowUserWhenDoesNotExistsByFollowerIdAndFolloweeId() {
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(testIdUser1, testIdUser2)).thenReturn(false);
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(testIdUser1, testIdUser2));
        assertEquals(exception.getMessage(), ErrorMessage.M_FOLLOW_DOES_NOT_EXIST);
        verify(subscriptionRepository, never()).unfollowUser(testIdUser1, testIdUser2);
    }
}