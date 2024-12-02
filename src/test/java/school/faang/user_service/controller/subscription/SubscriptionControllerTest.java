package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionRequestDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
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
    void followUserTest() {
        Long followerId = 1L;
        Long followeeId = 2L;
        SubscriptionRequestDto expectedSubscriptionRequestDto = new SubscriptionRequestDto(
                followerId,
                followeeId
        );

        Mockito.when(subscriptionService.followUser(followerId, followeeId))
                .thenReturn(expectedSubscriptionRequestDto);

        SubscriptionRequestDto actualSubscriptionRequestDto = subscriptionController.followUser(followerId, followeeId);

        assertEquals(expectedSubscriptionRequestDto, actualSubscriptionRequestDto);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void followUserThrowsDataValidationExceptionTest() {
        Long followerId = 1L;
        Long followeeId = 1L;
        String expectedExceptionMessage = "You cannot follow yourself";

         String actualExceptionMessage = assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followeeId))
                 .getMessage();

         assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(subscriptionService, Mockito.times(0))
                        .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void unfollowUserTest() {
        Long followerId = 1L;
        Long followeeId = 2L;
        SubscriptionRequestDto expectedSubscriptionRequestDto = new SubscriptionRequestDto(
                followerId,
                followeeId
        );

        Mockito.when(subscriptionService.unfollowUser(followerId, followeeId))
                .thenReturn(expectedSubscriptionRequestDto);

        SubscriptionRequestDto actualSubscriptionRequestDto = subscriptionController.unfollowUser(followerId, followeeId);

        assertEquals(expectedSubscriptionRequestDto, actualSubscriptionRequestDto);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());

    }

    @Test
    void unfollowUserThrowsDataValidationExceptionTest() {
        Long followerId = 1L;
        Long followeeId = 1L;
        String expectedExceptionMessage = "You cannot unfollow yourself";


        String actualExceptionMessage = assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followeeId))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(subscriptionService, Mockito.times(0))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getFollowersTest(){

        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();
        Long followeeId = 1L;

        List<SubscriptionUserDto> expectedSubscriptionUserDtosList = List.of();

        Mockito.when(subscriptionService.getFilteredFollowers(followeeId, subscriptionUserFilterDto))
                .thenReturn(expectedSubscriptionUserDtosList);

        List<SubscriptionUserDto> actualSubscriptionUserDtosList = subscriptionController.getFilteredFollowers(followeeId, subscriptionUserFilterDto);

        assertEquals(expectedSubscriptionUserDtosList, actualSubscriptionUserDtosList);


        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFilteredFollowers(followeeId, subscriptionUserFilterDto);
    }

    @Test
    void getFollowersCountTest() {
        Long followerId = 1L;

        Integer expectedFollowersAmount = 10;

        Mockito.when(subscriptionService.getFollowersCount(followerId))
                .thenReturn(expectedFollowersAmount);

        Integer actualFollowersAmount = subscriptionController.getFollowersCount(followerId);

        assertEquals(expectedFollowersAmount, actualFollowersAmount);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(followerId);
    }

    @Test
    void getFollowing() {
        Long followeeId = 1L;
        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();

        SubscriptionUserDto user1 = new SubscriptionUserDto();
        SubscriptionUserDto user2 = new SubscriptionUserDto();
        SubscriptionUserDto user3 = new SubscriptionUserDto();

        user1.setUsername("Sean");
        user2.setUsername("Mark");
        user3.setUsername("Mitch");

        List<SubscriptionUserDto> expectedSubscriptionUserDtoList = List.of(
                user1,
                user2,
                user3
        );

        Mockito.when(subscriptionService.getFollowing(followeeId, subscriptionUserFilterDto))
                .thenReturn(expectedSubscriptionUserDtoList);

        List<SubscriptionUserDto> actualSubscriptionUserDtoList = subscriptionController.getFollowing(followeeId, subscriptionUserFilterDto);

        assertEquals(expectedSubscriptionUserDtoList, actualSubscriptionUserDtoList);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(followeeId, subscriptionUserFilterDto);
    }

    @Test
    void getFollowingCount() {
        Long followerId = 1L;
        Integer expectedFollowingCount = 32;

        Mockito.when(subscriptionService.getFollowingCount(followerId))
                .thenReturn(expectedFollowingCount);

        Integer actualFollowingCount = subscriptionController.getFollowingCount(followerId);

        assertEquals(expectedFollowingCount, actualFollowingCount);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(followerId);
    }
}