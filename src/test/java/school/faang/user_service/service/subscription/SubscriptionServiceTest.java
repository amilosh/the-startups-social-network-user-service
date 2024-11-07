package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.client.ExpectedCount;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Mock
    UserMapper mapper;

    @InjectMocks
    SubscriptionService subscriptionService;


    @Test
    void followUserTest() throws DataValidationException {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);

        subscriptionService.followUser(Mockito.anyLong(), Mockito.anyLong());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void followUserThrowsDataValidationExceptionTest() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);

        String exceptionMessage = assertThrows(DataValidationException.class,
                () -> subscriptionService.followUser(Mockito.anyLong(), Mockito.anyLong()))
                .getMessage();

        assertEquals("The subscription already exists", exceptionMessage);


        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .followUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void unfollowUserTest() throws DataValidationException {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);

        subscriptionService.unfollowUser(Mockito.anyLong(), Mockito.anyLong());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void unfollowUserThrowsDataValidationExceptionTest() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);

        String exceptionMessage = assertThrows(DataValidationException.class,
                () -> subscriptionService.unfollowUser(Mockito.anyLong(), Mockito.anyLong())).getMessage();

        assertEquals("The subscription doesn't exist already", exceptionMessage);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsByFollowerIdAndFolloweeId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .unfollowUser(Mockito.anyLong(), Mockito.anyLong());

    }


    @Test
    void getFollowersTest() {
        // Test users
        User alexander = new User();
        User alexey = new User();
        User petr = new User();

        alexander.setUsername("Alexander");
        alexey.setUsername("alexey");
        petr.setUsername("Petr");

        Stream<User> userStream = Stream.of(
                alexander,
                alexey,
                petr
        );

        Long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("ale");

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(userStream);

        UserDto alexanderDto = new UserDto();
        UserDto alexeyDto = new UserDto();
        UserDto petrDto = new UserDto();

        alexanderDto.setUsername(alexander.getUsername());
        alexeyDto.setUsername(alexey.getUsername());
        petrDto.setUsername(petr.getUsername());


        List<UserDto> expectedUserDtoList = List.of(
                alexanderDto,
                alexeyDto
        );

        Mockito.when(mapper.toDto(alexander)).thenReturn(alexanderDto);
        Mockito.when(mapper.toDto(alexey)).thenReturn(alexeyDto);
//        throws an exception и and say that unnecessary stubbing, but I can't know whether current user pass the filter.
//        Apparently it's a feature of Mockito, can't do anything with that
//        Mockito.when(mapper.toDto(petr)).thenReturn(petrDto);

        // must return Alexander and Alexey ignoring case
        List<UserDto> actualUserDtoList = subscriptionService.getFollowers(followeeId, userFilterDto);

        assertEquals(expectedUserDtoList, actualUserDtoList);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowersThrowsNoSuchElementException() {
        Long followeeId = 1L;
        UserFilterDto userFilterDto = new UserFilterDto();

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(false);

        Exception exception = assertThrows(NoSuchElementException.class,
                () -> subscriptionService.getFollowers(followeeId, userFilterDto));

        assertEquals("Cannot find followee by id " + followeeId, exception.getMessage());

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowersCount() {
        Long followerId = 1L;

        Integer expectedFollowersAmount = 10;

        Mockito.when(subscriptionRepository.existsById(followerId))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(followerId))
                .thenReturn(expectedFollowersAmount);

        Integer actualFollowersAmount = subscriptionService.getFollowersCount(followerId);

        assertEquals(expectedFollowersAmount, actualFollowersAmount);


        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followerId);
    }

    @Test
    void getFollowersCountThrowsNoSuchElementException() {
        Long followerId = 1L;

        String expectedExceptionMessage = "Cannot find follower by id " + followerId;

        Mockito.when(subscriptionRepository.existsById(followerId))
                .thenReturn(false);

        String actualExceptionMessage = assertThrows(NoSuchElementException.class,
                () -> subscriptionService.getFollowersCount(followerId))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findFollowersAmountByFolloweeId(followerId);
    }
}