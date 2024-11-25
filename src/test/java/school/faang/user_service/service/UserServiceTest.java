package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

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

    @InjectMocks
    private UserService userService;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserRepository userRepository;

    private User user;
    private List<User> users;

    @BeforeEach
    void setUp() {
        var firstUser = User.builder().id(1L).build();
        var secondUser = User.builder().id(2L).build();
        var thirdUser = User.builder()
                .id(3L)
                .mentors(List.of(secondUser))
                .mentees(List.of(firstUser))
                .build();
        user = thirdUser;
        users = List.of(thirdUser);
    }

    @Test
    void testToGetUserDtoById_ShouldReturnCorrectDto() {
        when(userRepository.findById(3L))
                .thenReturn(Optional.of(user));

        var userDto = userService.getUserDtoById(3L);

        verify(userMapper, times(1)).toDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertNotNull(userDto);
    }

    @Test
    void testToGetUserDtoById_ShouldThrowException() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUserDtoById(user.getId()));
    }

    @Test
    void testToGetUserDtosByIds_ShouldReturnCorrectDtos() {
        var ids = List.of(3L);
        when(userRepository.findAllByIds(ids))
                .thenReturn(Optional.of(users));

        var userDtos = userService.getUserDtosByIds(ids);

        assertNotNull(userDtos);
        assertEquals(users.size(), userDtos.size());
    }

    @Test
    void testToGetUserDtosByIds_ShouldThrowException() {
        var ids = List.of(1L, 2L, 3L);
        when(userRepository.findAllByIds(ids))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> userService.getUserDtosByIds(ids));
    }
}
