package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    User user;
    UserDto userDto;
    List<UserDto> usersDto = new ArrayList<>();
    List<Long> ids = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);
        userDto = new UserDto(1L);
        usersDto = List.of(
                new UserDto(2L),
                new UserDto(3L));
        ids = List.of(2L, 3L);
    }

    @Test
    public void testGetUser() {
        when(userService.getUser(user.getId())).thenReturn(userDto);

        UserDto result = userController.getUser(user.getId());

        assertEquals(userDto, result);
    }

    @Test
    public void testGetUsers() {
        when(userService.getUsersByIds(ids)).thenReturn(usersDto);

        List<UserDto> result = userController.getUsersByIds(ids);

        assertEquals(usersDto, result);
    }
}