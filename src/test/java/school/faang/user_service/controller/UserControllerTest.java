package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.ValidationController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ValidationController validationController;

    @Test
    public void testGetUser() {
        Long id = 1L;
        UserDto userDto = new UserDto(1L);
        when(userService.getUser(id)).thenReturn(userDto);

        UserDto result = userController.getUser(id);

        verify(validationController, times(1)).validateIdCorrect(id);
        assertEquals(userDto, result);
    }

    @Test
    public void testGetUsers() {
        List<UserDto> usersDto = List.of(new UserDto(2L), new UserDto(3L));
        List<Long> ids = List.of(2L, 3L);
        when(userService.getUsersByIds(ids)).thenReturn(usersDto);

        List<UserDto> result = userController.getUsersByIds(ids);

        verify(validationController, times(1)).validateListIdsCorrect(ids);
        assertEquals(usersDto, result);
    }
}