package school.faang.user_service.service.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filter.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Mock
    SubscriptionUserMapper mapper;

    @Mock
    List<SubscriptionRequestFilter> requestFilters;

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
        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();
        subscriptionUserFilterDto.setNamePattern("ale");

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(userStream);

        List<SubscriptionRequestFilter> subscriptionFilters = List.of(
                                new UsernamePatternFilter(),
                                new AboutMePatternFilter()
        );

        Mockito.when(requestFilters.iterator())
                .thenAnswer(invocationOnMock -> subscriptionFilters.iterator());

        SubscriptionUserDto alexanderDto = new SubscriptionUserDto();
        SubscriptionUserDto alexeyDto = new SubscriptionUserDto();
        SubscriptionUserDto petrDto = new SubscriptionUserDto();

        alexanderDto.setUsername(alexander.getUsername());
        alexeyDto.setUsername(alexey.getUsername());
        petrDto.setUsername(petr.getUsername());


        List<SubscriptionUserDto> expectedSubscriptionUserDtoList = List.of(
                alexanderDto,
                alexeyDto
        );

        Mockito.when(mapper.toDto(alexander)).thenReturn(alexanderDto);
        Mockito.when(mapper.toDto(alexey)).thenReturn(alexeyDto);
//        throws an exception и and say that unnecessary stubbing, but I can't know whether current user pass the filter.
//        Apparently it's a feature of Mockito, can't do anything with that
//        Mockito.when(mapper.toDto(petr)).thenReturn(petrDto);

        // must return Alexander and Alexey ignoring case
        List<SubscriptionUserDto> actualSubscriptionUserDtoList = subscriptionService.getFilteredFollowers(followeeId, subscriptionUserFilterDto);

        assertEquals(expectedSubscriptionUserDtoList, actualSubscriptionUserDtoList);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowersThrowsNoSuchElementException() {
        Long followeeId = 1L;
        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(false);

        Exception exception = assertThrows(NoSuchElementException.class,
                () -> subscriptionService.getFilteredFollowers(followeeId, subscriptionUserFilterDto));

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

    @Test
    void getFollowingTest() {
        // init data
        Long followeeId = 1L;
        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();

        subscriptionUserFilterDto.setAboutMePattern("er");

        // users
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        user1.setAboutMe("farmer");
        user1.setUsername("Michael");

        user2.setAboutMe("hiker");
        user2.setUsername("George");

        user3.setUsername("Beau");
        user3.setAboutMe("millennial");

        Stream<User> followeeStream = Stream.of(
                user1,
                user2,
                user3
        );


        // dtos
        SubscriptionUserDto user1Dto = new SubscriptionUserDto();
        SubscriptionUserDto user2Dto = new SubscriptionUserDto();
        // third is not going to pass a filter

        user1Dto.setUsername(user1.getUsername());
        user2Dto.setUsername(user2.getUsername());

        List<SubscriptionUserDto> expectedSubscriptionUserDtoList = List.of(
                user1Dto,
                user2Dto
        );


        // mocking
        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(followeeStream);

        List<SubscriptionRequestFilter> requestFilterList = List.of(
                        new EmailPatternFilter(),
                        new AboutMePatternFilter());

        Mockito.when(requestFilters.iterator())
                .thenAnswer(invocationOnMock -> requestFilterList.iterator());

        Mockito.when(mapper.toDto(user1)).thenReturn(user1Dto);
        Mockito.when(mapper.toDto(user2)).thenReturn(user2Dto);


        // result
        List<SubscriptionUserDto> actualSubscriptionUserDtoList = subscriptionService.getFollowing(followeeId, subscriptionUserFilterDto);

        // assertions
        assertEquals(expectedSubscriptionUserDtoList, actualSubscriptionUserDtoList);

        // verifies
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFolloweeId(followeeId);
        Mockito.verify(requestFilters, Mockito.times(3))
                .iterator();
    }

    @Test
    void getFollowingThrowsNoSuchElementException() {
        Long followeeId = 1L;
        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();

        String expectedExceptionMessage = "Cannot find followee by id " + followeeId;

        Mockito.when(subscriptionRepository.existsById(followeeId))
                .thenReturn(false);

        String actualExceptionMessage = assertThrows(NoSuchElementException.class,
                () -> subscriptionService.getFollowing(followeeId, subscriptionUserFilterDto))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findByFolloweeId(followeeId);
    }

    @Test
    void getFollowingCount() {
        Long followerId = 1L;

        Integer expectedFollowingCount = 41;

        Mockito.when(subscriptionRepository.existsById(followerId))
                .thenReturn(true);
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId))
                .thenReturn(expectedFollowingCount);

        Integer actualFollowingCount = subscriptionService.getFollowingCount(followerId);

        assertEquals(expectedFollowingCount, actualFollowingCount);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    void getFollowingCountThrowsNoSuchElementException() {
        Long followerId = 1L;

        String expectedExceptionMessage = "Cannot find followee by id " + followerId;

        Mockito.when(subscriptionRepository.existsById(followerId))
                .thenReturn(false);

        String actualExceptionMessage = assertThrows(NoSuchElementException.class,
                () -> subscriptionService.getFollowingCount(followerId))
                .getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);

        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .existsById(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(0))
                .findFolloweesAmountByFollowerId(followerId);

    }
}