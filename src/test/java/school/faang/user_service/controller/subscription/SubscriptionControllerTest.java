package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    SubscriptionController subscriptionController;


    @Test
    void followUserTest() throws DataValidationException {
        Long followerId = 1L;
        Long followeeId = 2L;

        ResponseEntity<?> responseEntity = subscriptionController.followUser(followerId, followeeId);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void followUserThrowsDataValidationExceptionTest() throws Exception {
        Long followerId = 1L;
        Long followeeId = 1L;

        assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId));

        Mockito.verify(subscriptionService, Mockito.times(0))
                        .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void unfollowUserTest() throws DataValidationException {
        Long followerId = 1L;
        Long followeeId = 2L;

        ResponseEntity<?> responseEntity = subscriptionController.unfollowUser(followerId, followeeId);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());

    }

    @Test
    void unfollowUserThrowsDataValidationExceptionTest() throws Exception {
        Long followerId = 1L;
        Long followeeId = 1L;

        assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followeeId));

        Mockito.verify(subscriptionService, Mockito.times(0))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getFollowersTest(){

        UserFilterDto userFilterDto = new UserFilterDto();
        Long followeeId = 1L;

        Mockito.when(subscriptionService.getFollowers(followeeId, userFilterDto))
                .thenReturn(List.of());

        ResponseEntity<?> responseEntity = subscriptionController.getFollowers(followeeId, userFilterDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(List.of(), responseEntity.getBody());

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(followeeId, userFilterDto);
    }
}