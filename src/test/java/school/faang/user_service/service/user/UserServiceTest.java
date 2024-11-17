package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUserByIdNotFound() {
        UserDto user = createUserDto();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(user.getId()));

        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testGetUserByIdSuccess() {
        UserDto userDto = createUserDto();
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(userDto.getId());

        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository);

        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    public void testGetUsersEmptyList() {
        UserDto firstUser = createUserDto();
        UserDto secondUser = createUserDto();
        secondUser.setId(2L);
        List<Long> userIds = List.of(firstUser.getId(), secondUser.getId());
        when(userRepository.findAllById(userIds)).thenReturn(new ArrayList<>());
        List<UserDto> result = userService.getUsers(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verifyNoMoreInteractions(userRepository);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testGetUsersSuccess() {
        UserDto firstUserDto = createUserDto();
        UserDto secondUserDto = createUserDto();
        secondUserDto.setId(2L);
        secondUserDto.setUsername("tomcat");
        List<Long> userIds = List.of(firstUserDto.getId(), secondUserDto.getId());
        User firstUser = new User();
        firstUser.setId(firstUserDto.getId());
        firstUser.setUsername(firstUserDto.getUsername());

        User secondUser = new User();
        secondUser.setId(secondUserDto.getId());
        secondUser.setUsername(secondUserDto.getUsername());

        when(userRepository.findAllById(userIds)).thenReturn(List.of(firstUser, secondUser));
        when(userMapper.toDto(firstUser)).thenReturn(firstUserDto);
        when(userMapper.toDto(secondUser)).thenReturn(secondUserDto);
        List<UserDto> result = userService.getUsers(userIds);

        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(2)).toDto(any(User.class));
        verifyNoMoreInteractions(userRepository);
        assertEquals(result.size(), 2);
        assertEquals(firstUserDto.getId(), result.get(0).getId());
        assertEquals(secondUserDto.getId(), result.get(1).getId());
        assertEquals(firstUser.getUsername(), result.get(0).getUsername());
        assertEquals(secondUser.getUsername(), result.get(1).getUsername());
    }

    private UserDto createUserDto() {
        UserDto user = UserDto.builder()
                .id(1L)
                .username("johndoe")
                .email("johndoe@example.com")
                .userProfilePicFileId("https://amazon.com/s3/profilepic.jpg")
                .premiumId(12L).build();
        return user;
    }


}
