package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    void testFollowUser() {
        long followerId = 1;
        long followeeId = 2;
        assertDoesNotThrow(() -> subscriptionController.followUser(followerId, followeeId));
        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void testUnfollowUser() {
        long followerId = 1;
        long followeeId = 2;
        assertDoesNotThrow(() -> subscriptionController.unfollowUser(followerId, followeeId));
        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void testGetFollowers() {
        long followeeId = 2;
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> expectedFollowers = Collections.singletonList(new UserDto());
        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(expectedFollowers);
        List<UserDto> actualFollowers = subscriptionController.getFollowers(followeeId, filter);
        verify(subscriptionService, times(1)).getFollowers(followeeId, filter);
        assertEquals(expectedFollowers, actualFollowers, "Список подписчиков должен соответствовать ожидаемому списку.");
    }

    @Test
    void testGetFollowersCount() {
        long followerId = 1;
        long expectedCount = 5;
        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);
        long actualCount = subscriptionController.getFollowersCount(followerId);
        verify(subscriptionService, times(1)).getFollowersCount(followerId);
        assertEquals(expectedCount, actualCount, "Количество подписчиков должно соответствовать ожидаемому количеству.");
    }

    @Test
    void testGetFollowing() {
        long followeeId = 2;
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> expectedFollowing = Collections.singletonList(new UserDto());
        when(subscriptionService.getFollowing(followeeId, filter)).thenReturn(expectedFollowing);
        List<UserDto> actualFollowing = subscriptionController.getFollowing(followeeId, filter);
        verify(subscriptionService, times(1)).getFollowing(followeeId, filter);
        assertEquals(expectedFollowing, actualFollowing, "Список следующих пользователей должен соответствовать ожидаемому списку.");
    }

    @Test
    void testGetFollowingCount() {
        long followerId = 1;
        long expectedCount = 3;
        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectedCount);
        long actualCount = subscriptionController.getFollowingCount(followerId);
        verify(subscriptionService, times(1)).getFollowingCount(followerId);
        assertEquals(expectedCount, actualCount, "Следующее количество должно соответствовать ожидаемому количеству.");
    }
}