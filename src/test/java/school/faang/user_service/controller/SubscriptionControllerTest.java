package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.FollowDto;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    void followUser_shouldCallServiceAndReturnOk() {
        FollowDto followDto = new FollowDto(1L, 2L);

        ResponseEntity<Void> response = subscriptionController.followUser(followDto);

        verify(subscriptionService).followUser(1L, 2L);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void unfollowUser_shouldCallServiceAndReturnOk() {
        FollowDto followDto = new FollowDto(1L, 2L);

        ResponseEntity<Void> response = subscriptionController.unfollowUser(followDto);

        verify(subscriptionService).unfollowUser(1L, 2L);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void getFollowersCount_shouldReturnCount() {
        long followerId = 1L;
        long expectedCount = 5L;
        when(subscriptionService.getFollowersCount(followerId)).thenReturn(expectedCount);

        ResponseEntity<Long> response = subscriptionController.getFollowersCount(followerId);

        assertEquals(expectedCount, response.getBody());
        assertEquals(ResponseEntity.ok(expectedCount), response);
    }

    @Test
    void getFollowingCount_shouldReturnCount() {
        long followerId = 1L;
        long expectedCount = 3L;
        when(subscriptionService.getFollowingCount(followerId)).thenReturn(expectedCount);

        ResponseEntity<Long> response = subscriptionController.getFollowingCount(followerId);

        assertEquals(expectedCount, response.getBody());
        assertEquals(ResponseEntity.ok(expectedCount), response);
    }
}
