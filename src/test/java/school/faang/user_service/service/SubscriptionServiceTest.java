package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.user_filter.UserEmailFilter;
import school.faang.user_service.filter.user_filter.UserFilter;
import school.faang.user_service.filter.user_filter.UserNameFilter;
import school.faang.user_service.service.validation.SubscriptionValidation;
import school.faang.user_service.service.validation.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private SubscriptionValidation subscriptionValidation;

    @Mock
    private UserValidation userValidation;

    @InjectMocks
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

    @BeforeEach
    public void setUp() {
        userId = 10L;
        followerId = 1L;
        followeeId = 2L;
        followingsAmount = 3;

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

        filter = new UserFilterDto();

        userFilters = new ArrayList<>();
        userFilters.add(mock(UserNameFilter.class));
        userFilters.add(mock(UserEmailFilter.class));

        firstUserDto = new UserDto(followerId, "firstUser", "first@email.com");
        secondUserDto = new UserDto(followeeId, "secondUser", "second@email.com");
        expectedUsers = new ArrayList<>(List.of(firstUserDto, secondUserDto));

        users = Stream.of(firstUser, secondUser);
    }

    @Test
    public void followUserTest() {
        arrangeUserValidation(followerId, followeeId);
        doNothing().when(subscriptionValidation).isFollowingExistsValidate(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);

        verifyUserValidation(followerId, followeeId);
        verify(subscriptionValidation).isFollowingExistsValidate(followerId, followeeId);
        verify(subscriptionRepository).followUser(followerId, followeeId);
    }

    @Test
    public void unfollowUserTest() {
        arrangeUserValidation(followerId, followeeId);
        doNothing().when(subscriptionValidation).isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionService.unfollowUser(followerId, followeeId);

        verify(userValidation).isUserExists(followerId, followeeId);
        verify(subscriptionValidation).isFollowingNotExistsValidate(followerId, followeeId);
        verify(subscriptionRepository).unfollowUser(followerId, followeeId);
    }

    @Test
    public void getFollowersTest() {
        arrangeUserValidation(userId);
        when(subscriptionRepository.findByFolloweeId(userId)).thenReturn(users);
        arrangeMapping();

        List<UserDto> result = subscriptionService.getFollowers(userId, filter);

        verifyUserValidation(userId);
        verify(subscriptionRepository).findByFolloweeId(userId);
        verifyMapping();

        assertEquals(2, result.size());
        assertArrayEquals(expectedUsers.toArray(), result.toArray());
    }

    @Test
    public void getFollowersCountTest() {
        arrangeUserValidation(followeeId);
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(followingsAmount);

        int actualResult = subscriptionService.getFollowersCount(followeeId);

        verifyUserValidation(followeeId);
        verify(subscriptionRepository).findFollowersAmountByFolloweeId(followeeId);

        assertEquals(followingsAmount, actualResult);
    }

    @Test
    public void getFollowingTest() {
        arrangeUserValidation(userId);
        when(subscriptionRepository.findByFollowerId(userId)).thenReturn(users);
        arrangeMapping();

        List<UserDto> result = subscriptionService.getFollowing(userId, filter);

        verifyUserValidation(userId);
        verify(subscriptionRepository).findByFollowerId(userId);
        verifyMapping();

        assertEquals(2, result.size());
        assertArrayEquals(expectedUsers.toArray(), result.toArray());
    }

    @Test
    public void getFollowingCountTest() {
        arrangeUserValidation(followeeId);
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followeeId)).thenReturn(followingsAmount);

        int actualResult = subscriptionService.getFollowingCount(followeeId);

        verifyUserValidation(followeeId);
        verify(subscriptionRepository).findFolloweesAmountByFollowerId(followeeId);

        assertEquals(followingsAmount, actualResult);
    }

    private void arrangeUserValidation(long userId) {
        doNothing().when(userValidation).isUserExists(userId);
    }

    private void arrangeUserValidation(long firstUserId, long secondUserId) {
        doNothing().when(userValidation).isUserExists(firstUserId, secondUserId);
    }

    private void arrangeMapping() {
        when(userMapper.entityStreamToDtoList(users)).thenReturn(expectedUsers);
    }

    private void verifyUserValidation(long userId) {
        verify(userValidation).isUserExists(userId);
    }

    private void verifyUserValidation(long firstUserId, long secondUserId) {
        verify(userValidation).isUserExists(firstUserId, secondUserId);

    }

    private void verifyMapping() {
        verify(userMapper).entityStreamToDtoList(users);
    }

}
