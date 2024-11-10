package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Spy
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    UserService userService;

    @Test
    void getUserTest() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("username")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void getUserNotFoundTest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> userService.getUser(userId)
        );
        assertEquals(String.format(ErrorMessage.USER_NOT_FOUND, userId), exception.getMessage());
    }

    @Test
    void getUsersIdsTest() {
        List<Long> userIds = List.of(1L, 2L);
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getUsers(userIds);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUsersNotFoundTest() {
        List<Long> userIds = List.of(1L, 2L);
        when(userRepository.findAllById(userIds)).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers(userIds);

        assertTrue(result.isEmpty());
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
}