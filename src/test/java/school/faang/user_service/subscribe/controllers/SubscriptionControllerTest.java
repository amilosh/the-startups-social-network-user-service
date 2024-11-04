package school.faang.user_service.subscribe.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import school.faang.user_service.controllers.subscribe.SubscriptionController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.dto.UserFilterDTO;
import school.faang.user_service.services.subscribe.SubscriptionService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFollowUser() {
        Long followerId = 1L;
        Long followeeId = 2L;

        subscriptionController.followUser(followerId, followeeId);

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void testUnfollowUser() {
        Long followerId = 1L;
        Long followeeId = 2L;

        subscriptionController.unfollowUser(followerId, followeeId);

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void testGetFollowers() {
        Long userId = 1L;
        UserFilterDTO filter = new UserFilterDTO();
        UserDTO userDTO = new UserDTO(1L, "username", "email@example.com");
        when(subscriptionService.getFollowers(userId, filter)).thenReturn(Collections.singletonList(userDTO));

        List<UserDTO> followers = subscriptionController.getFollowers(userId, filter);

        assertEquals(1, followers.size());
        verify(subscriptionService, times(1)).getFollowers(userId, filter);
    }

    @Test
    void testGetFollowersCount() {
        Long userId = 1L;
        long expectedCount = 5;
        when(subscriptionService.countFollowers(userId)).thenReturn(expectedCount);

        long count = subscriptionController.getFollowersCount(userId);

        assertEquals(expectedCount, count);
        verify(subscriptionService, times(1)).countFollowers(userId);
    }

    @Test
    void testGetFollowing() {
        Long userId = 1L;
        UserFilterDTO filter = new UserFilterDTO();
        UserDTO userDTO = new UserDTO(1L, "username", "email@example.com");
        when(subscriptionService.getFollowing(userId, filter)).thenReturn(Collections.singletonList(userDTO));

        List<UserDTO> following = subscriptionController.getFollowing(userId, filter);

        assertEquals(1, following.size());
        verify(subscriptionService, times(1)).getFollowing(userId, filter);
    }
}
