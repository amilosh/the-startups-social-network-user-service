package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.validation.SubscriptionValidation;
import school.faang.user_service.service.validation.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Mock
    private SubscriptionValidation subscriptionValidation;

    @Mock
    private UserValidation userValidation;

    SubscriptionService subscriptionService;

    private UserFilterDto filter;
    private User firstUser;
    private User secondUser;
    private UserDto firstUserDto;
    private UserDto secondUserDto;

    private long userId;
    private long followerId;
    private long followeeId;
    private int followingsAmount;

    private Stream<User> users;
    private List<UserDto> expectedUsers;
    private List<UserFilter> userFilters;

    @BeforeEach
    public void setUp() {
        UserFilter MockUserNameFilter = mock(UserNameFilter.class);
        UserFilter MockUserEmailFilter = mock(UserEmailFilter.class);
        userFilters = new ArrayList<>(List.of(MockUserNameFilter, MockUserEmailFilter));

        subscriptionService = new SubscriptionService(subscriptionRepository, userMapper,
                userFilters, subscriptionValidation, userValidation);
    }

    @Test
    public void followUserTest() {
        followerId = 1L;
        followeeId = 2L;
        doNothing().when(userValidation).areUsersExist(followerId, followeeId);
        doNothing().when(subscriptionValidation).isFollowingExistsValidate(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);

        verify(userValidation).areUsersExist(followerId, followeeId);
        verify(subscriptionValidation).isFollowingExistsValidate(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void unfollowUserTest() {
        followerId = 1L;
        followeeId = 2L;
        doNothing().when(userValidation).areUsersExist(followerId, followeeId);
        doNothing().when(subscriptionValidation).isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(userValidation).areUsersExist(followerId, followeeId);
        verify(subscriptionValidation).isFollowingNotExistsValidate(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void getFollowersTest() {
        followerId = 1L;
        followeeId = 2L;
        userId = 10L;

        firstUser = User.builder()
                .id(followerId)
                .username("firstUser")
                .email("first@email.com")
                .build();

        secondUser = User.builder()
                .id(followeeId)
                .username("secondUser")
                .email("second@email.com")
                .build();

        users = Stream.of(firstUser, secondUser);

        filter = UserFilterDto.builder()
                .namePattern("first")
                .emailPattern("first")
                .build();

        firstUserDto = new UserDto(followerId, "firstUser", "first@email.com");
        secondUserDto = new UserDto(followeeId, "secondUser", "second@email.com");
        expectedUsers = new ArrayList<>(List.of(firstUserDto, secondUserDto));

        doNothing().when(userValidation).isUserExists(userId);
        when(subscriptionRepository.findByFolloweeId(userId)).thenReturn(users);
        when(userFilters.get(0).isApplicable(filter)).thenReturn(true);
        when(userFilters.get(0).apply(users, filter)).thenReturn(users);
        when(userFilters.get(1).isApplicable(filter)).thenReturn(false);

        List<UserDto> result = subscriptionService.getFollowers(userId, filter);

        verify(userValidation).isUserExists(userId);
        verify(subscriptionRepository).findByFolloweeId(userId);
        verify(userMapper).entityStreamToDtoList(users);
        verify(userFilters.get(0)).isApplicable(filter);
        verify(userFilters.get(1)).isApplicable(filter);
        verify(userFilters.get(0)).apply(users, filter);

        assertEquals(expectedUsers.size(), result.size());
        assertEquals(expectedUsers.get(0), result.get(0));
        assertEquals(expectedUsers.get(1), result.get(1));
    }

    @Test
    public void getFollowersCountTest() {
        followerId = 1L;
        followingsAmount = 3;
        doNothing().when(userValidation).isUserExists(followeeId);
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followingsAmount);

        int actualResult = subscriptionService.getFollowersCount(followeeId);

        verify(userValidation).isUserExists(followeeId);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);

        assertEquals(followingsAmount, actualResult);
    }

    @Test
    public void getFollowingTest() {
        followerId = 1L;
        followeeId = 2L;
        userId = 10L;

        firstUser = User.builder()
                .id(followerId)
                .username("firstUser")
                .email("first@email.com")
                .build();

        secondUser = User.builder()
                .id(followeeId)
                .username("secondUser")
                .email("second@email.com")
                .build();

        users = Stream.of(firstUser, secondUser);

        filter = UserFilterDto.builder()
                .namePattern("first")
                .emailPattern("first")
                .build();

        firstUserDto = new UserDto(followerId, "firstUser", "first@email.com");
        secondUserDto = new UserDto(followeeId, "secondUser", "second@email.com");
        expectedUsers = new ArrayList<>(List.of(firstUserDto, secondUserDto));

        doNothing().when(userValidation).isUserExists(userId);
        when(subscriptionRepository.findByFollowerId(userId)).thenReturn(users);
        when(userFilters.get(0).isApplicable(filter)).thenReturn(true);
        when(userFilters.get(0).apply(users, filter)).thenReturn(users);
        when(userFilters.get(1).isApplicable(filter)).thenReturn(true);
        when(userFilters.get(1).apply(users, filter)).thenReturn(users);

        List<UserDto> result = subscriptionService.getFollowing(userId, filter);

        verify(userValidation).isUserExists(userId);
        verify(subscriptionRepository).findByFollowerId(userId);
        verify(userMapper).entityStreamToDtoList(users);
        verify(userFilters.get(0)).isApplicable(filter);
        verify(userFilters.get(0)).apply(users, filter);
        verify(userFilters.get(1)).isApplicable(filter);
        verify(userFilters.get(1)).apply(users, filter);

        assertEquals(2, result.size());
        assertEquals(expectedUsers.get(0), result.get(0));
        assertEquals(expectedUsers.get(1), result.get(1));
    }

    @Test
    public void getFollowingCountTest() {
        followeeId = 2L;
        followingsAmount = 3;
        doNothing().when(userValidation).isUserExists(followeeId);
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followeeId)).thenReturn(followingsAmount);

        int actualResult = subscriptionService.getFollowingCount(followeeId);

        verify(userValidation).isUserExists(followeeId);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followeeId);

        assertEquals(followingsAmount, actualResult);
    }
}