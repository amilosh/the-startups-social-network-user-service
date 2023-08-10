package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void getUser_Test() {
        User user1 = User.builder()
                .id(1)
                .build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));

        UserDto result = userService.getUser(1);

        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(userMapper).toUserDto(user1);
    }

    @Test
    void getUser_UserNotFound_Test() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(1L));

        assertEquals("User with id 1 not found", exception.getMessage());

        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    void getUsersByIds_Test() {
        User user1 = User.builder()
                .id(1)
                .build();

        User user2 = User.builder()
                .id(2)
                .build();

        List<User> users = List.of(user1, user2);

        Mockito.when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(users);

        List<UserDto> usersByIds = userService.getUsersByIds(List.of(1L, 2L));

        Mockito.verify(userRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void getUsersByIds_ReturnEmptyList_Test() {

        Mockito.when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(new ArrayList<>());

        List<UserDto> usersByIds = userService.getUsersByIds(List.of(1L, 2L));

        Mockito.verify(userRepository).findAllById(List.of(1L, 2L));

        Assertions.assertEquals(0, usersByIds.size());
    }
}