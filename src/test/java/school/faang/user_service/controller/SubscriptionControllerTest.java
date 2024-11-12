package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFollowUser_Success() {
        long followerId = 1L;
        long followeeId = 2L;
        subscriptionController.followUser(followerId, followeeId);
        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void testFollowUser_SelfFollow_ThrowsException() {
        long followerId = 1L;
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionController.followUser(followerId, followerId);
        });
        assertEquals("Подписаться на самого себя запрещено.", exception.getMessage());
    }

    @Test
    void testUnfollowUser_Success() {
        long followerId = 1L;
        long followeeId = 2L;
        subscriptionController.unfollowUser(followerId, followeeId);
        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void testUnfollowUser_SelfUnfollow_ThrowsException() {
        long followerId = 1L;
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            subscriptionController.unfollowUser(followerId, followerId);
        });
        assertEquals("Отписаться от самого себя нельзя.", exception.getMessage());
    }

    @Test
    void testGetFollowers() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> expectedFollowers = Collections.singletonList(new UserDto());
        when(subscriptionService.getFollowers(followeeId, filter)).thenReturn(expectedFollowers);
        List<UserDto> actualFollowers = subscriptionController.getFollowers(followeeId, filter);
        assertEquals(expectedFollowers, actualFollowers);
        verify(subscriptionService, times(1)).getFollowers(followeeId, filter);
    }

    @Test
    void testGetFollowersCount() {
        long followerId = 1L;
        long expectedCount = 5L;
        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);
        long actualCount = subscriptionController.getFollowersCount(followerId);
        assertEquals(expectedCount, actualCount);
        verify(subscriptionService, times(1)).getFollowersCount(followerId);
    }

    @Test
    void testGetFollowing() {
        long followeeId = 1L;
        UserFilterDto filter = new UserFilterDto();
        List<UserDto> expectedFollowing = Collections.singletonList(new UserDto());
        when(subscriptionService.getFollowing(followeeId, filter)).thenReturn(expectedFollowing);
        List<UserDto> actualFollowing = subscriptionController.getFollowing(followeeId, filter);
        assertEquals(expectedFollowing, actualFollowing);
        verify(subscriptionService, times(1)).getFollowing(followeeId, filter);
    }

    @Test
    void testGetFollowingCount() {
        long followerId = 1L;
        long expectedCount = 3L;
        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectedCount);
        long actualCount = subscriptionController.getFollowingCount(followerId);
        assertEquals(expectedCount, actualCount);
        verify(subscriptionService, times(1)).getFollowingCount(followerId);
    }
}
