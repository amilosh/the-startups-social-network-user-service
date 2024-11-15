package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Spy
    UserMapper userMapper;

    private UserService userService;
    private List<UserFilter> userFilters;

    @BeforeEach
    void setUp() {
        UserFilter userEmailFilter = mock(UserEmailFilter.class);
        UserFilter userNameFilter = mock(UserNameFilter.class);
        userFilters = new ArrayList<>(List.of(userEmailFilter, userNameFilter));
        userMapper = new UserMapperImpl();

        userService = new UserService(userRepository, userFilters, userMapper);
    }

    @Test
    void existsByIdTrue() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertTrue(userService.existsById(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void existsByIdFalse() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertFalse(userService.existsById(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void saveUser() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getNotExistingUsersEmptyListTest() {
        List<Long> userIds = Collections.emptyList();
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(userIds);

        assertTrue(notExistingUserIds.isEmpty());
    }

    @Test
    void getNotExistingUsersValidListTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        when(userRepository.findNotExistingUserIds(userIds)).thenReturn(List.of(1L));

        List<Long> notExistingUserIds = userService.getNotExistingUserIds(userIds);

        assertEquals(1, notExistingUserIds.size());
        assertTrue(notExistingUserIds.contains(1L));
    }

    @Test
    void getPremiumUsersTest() {
        long firstUserId = 1L;
        long secondUserId = 2L;

        User firstUser = User.builder()
                .id(firstUserId)
                .username("firstUser")
                .email("first@email.com")
                .build();

        User secondUser = User.builder()
                .id(secondUserId)
                .username("secondUser")
                .email("second@email.com")
                .build();

        UserDto firstUserDto = new UserDto(firstUserId, "firstUser", "first@email.com");
        UserDto secondUserDto = new UserDto(secondUserId, "secondUser", "second@email.com");

        Stream<User> users = Stream.of(firstUser, secondUser);
        List<UserDto> expectedUsersDto = List.of(firstUserDto, secondUserDto);
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findPremiumUsers()).thenReturn(users);
        when(userFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(userFilters.get(0).apply(users, filterDto)).thenReturn(users);
        when(userFilters.get(1).isApplicable(filterDto)).thenReturn(false);

        List<UserDto> actualUsersDto = userService.getPremiumUsers(filterDto);

        verify(userRepository).findPremiumUsers();
        verify(userFilters.get(0)).isApplicable(filterDto);
        verify(userFilters.get(1)).isApplicable(filterDto);
        verify(userFilters.get(0)).apply(users, filterDto);

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    void getNotPremiumUsersTest() {
        long firstUserId = 1L;
        long secondUserId = 2L;

        Premium expiredPremium = new Premium();
        expiredPremium.setEndDate(LocalDateTime.now().minusDays(1));

        User firstUser = User.builder()
                .id(firstUserId)
                .username("firstUser")
                .email("first@email.com")
                .premium(expiredPremium)
                .build();

        User secondUser = User.builder()
                .id(secondUserId)
                .username("secondUser")
                .email("second@email.com")
                .build();

        UserDto firstUserDto = new UserDto(firstUserId, "firstUser", "first@email.com");
        UserDto secondUserDto = new UserDto(secondUserId, "secondUser", "second@email.com");

        List<UserDto> expectedUsersDto = List.of(firstUserDto, secondUserDto);
        List<User> usersList = List.of(firstUser, secondUser);
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findAll()).thenReturn(usersList);
        when(userFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(userFilters.get(0).apply(any(), eq(filterDto)))
                .thenAnswer(invocation -> invocation.<Stream<User>>getArgument(0));
        when(userFilters.get(1).isApplicable(filterDto)).thenReturn(false);

        List<UserDto> actualUsersDto = userService.getNotPremiumUsers(filterDto);

        verify(userFilters.get(0), times(1)).isApplicable(filterDto);
        verify(userFilters.get(1), times(1)).isApplicable(filterDto);
        verify(userFilters.get(0), times(1)).apply(any(), eq(filterDto));

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    public void usersArePremiumShouldReturnEmptyListTest() {
        Premium premium = new Premium();
        premium.setEndDate(LocalDateTime.now().plusDays(30));

        User user = new User();
        user.setPremium(premium);
        User user2 = new User();
        user2.setPremium(premium);

        List<User> users = new ArrayList<>(List.of(user, user2));
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actualUsers = userService.getNotPremiumUsers(filterDto);

        assertEquals(new ArrayList<>(), actualUsers);
    }
}