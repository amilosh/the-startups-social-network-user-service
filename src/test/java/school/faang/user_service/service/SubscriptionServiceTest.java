package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void followUser_shouldThrowException_whenAlreadyFollowing() {
        long followerId = 1;
        long followeeId = 2;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionService.followUser(followerId, followeeId);
        });
        assertEquals("Подписка уже существует.", exception.getMessage());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    void followUser_shouldFollowUser_whenNotFollowing() {
        long followerId = 1;
        long followeeId = 2;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    void unfollowUser_shouldUnfollowUser() {
        long followerId = 1;
        long followeeId = 2;
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowersCount_shouldReturnCount() {
        long followeeId = 1;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(5);
        long count = subscriptionService.getFollowersCount(followeeId);
        assertEquals(5, count);
    }

    @Test
    void getFollowingCount_shouldReturnCount() {
        long followerId = 1;
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(3);
        long count = subscriptionService.getFollowingCount(followerId);
        assertEquals(3, count);
    }
}
