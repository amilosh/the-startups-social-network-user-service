package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("user@gmail.com")
                .build();
    }

    @Test
    void testGetUserById_UserExists() {
        // arrange
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        // act
        User foundUser = userService.getUserById(1L);

        // assert
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserDoesNotExist() {
        // arrange
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testGetAllUsersByIds() {
        // arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        List<User> users = Arrays.asList(user, new User());

        when(userRepo.findAllById(ids)).thenReturn(users);

        // act
        List<User> foundUsers = userService.getAllUsersByIds(ids);

        // assert
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        verify(userRepo, times(1)).findAllById(ids);
    }

    @Test
    void testGetUserDtoById() {
        // arrange
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // act
        UserDto foundUserDto = userService.getUserDtoById(1L);

        // assert
        assertNotNull(foundUserDto);
        assertEquals(userDto.id(), foundUserDto.id());
        verify(userRepo, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void testGetAllUsersDtoByIds() {
        // arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        List<User> users = Arrays.asList(user, new User());
        List<UserDto> userDtos = Arrays.asList(userDto, new UserDto(2L, "anotherUser", "anotherUser@gmail.com"));

        when(userRepo.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(users)).thenReturn(userDtos);

        // act
        List<UserDto> foundUserDtos = userService.getAllUsersDtoByIds(ids);

        // assert
        assertNotNull(foundUserDtos);
        assertEquals(2, foundUserDtos.size());
        verify(userRepo, times(1)).findAllById(ids);
        verify(userMapper, times(1)).toDto(users);
    }
}
