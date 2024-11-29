package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.AvatarServiceValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Mock
    private AvatarServiceValidator avatarServiceValidator;

    @InjectMocks
    private AvatarService avatarService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("validUsername");
    }

    @Test
    public void generateAvatarShouldCallValidatorWhenUserIsValid() {
        avatarService.generateAvatar(user);

        verify(avatarServiceValidator, times(1)).checkUser (user);
    }

    @Test
    public void generateAvatarShouldReturnAvatarUrlWhenUserIsValid() {
        String expectedUrl = "https://api.dicebear.com/9.x/avataaars/svg?seed=validUsername";

        String actualUrl = avatarService.generateAvatar(user);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void generateAvatar_ShouldThrowException_WhenValidatorThrowsException() {
        doThrow(new IllegalArgumentException("Пустой пользователь или его имя"))
                .when(avatarServiceValidator).checkUser (user);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            avatarService.generateAvatar(user);
        });

        assertEquals("Пустой пользователь или его имя", exception.getMessage());
    }
}