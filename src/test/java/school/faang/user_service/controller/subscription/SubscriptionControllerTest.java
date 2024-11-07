package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
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

    @Test
    void getFollowersCountTest() {
        Long followerId = 1L;

        Integer expectedFollowersAmount = 10;

        Mockito.when(subscriptionService.getFollowersCount(followerId))
                .thenReturn(expectedFollowersAmount);

        ResponseEntity<Integer> responseEntity = subscriptionController.getFollowersCount(followerId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedFollowersAmount, responseEntity.getBody());

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(followerId);
    }

    @Test
    void getFollowing() {
        Long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        UserDto user1 = new UserDto();
        UserDto user2 = new UserDto();
        UserDto user3 = new UserDto();

        user1.setUsername("Sean");
        user2.setUsername("Mark");
        user3.setUsername("Mitch");

        List<UserDto> expectedUserDtoList = List.of(
                user1,
                user2,
                user3
        );

        Mockito.when(subscriptionService.getFollowing(followeeId, userFilterDto))
                .thenReturn(expectedUserDtoList);

        ResponseEntity<List<UserDto>> response = subscriptionController.getFollowing(followeeId, userFilterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDtoList, response.getBody());

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(followeeId, userFilterDto);
    }

    @Test
    void getFollowingCount() {
        Long followerId = 1L;
        Integer expectedFollowingCount = 32;

        Mockito.when(subscriptionService.getFollowingCount(followerId))
                .thenReturn(expectedFollowingCount);

        ResponseEntity<Integer> response = subscriptionController.getFollowingCount(followerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedFollowingCount, response.getBody());

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(followerId);
    }
}