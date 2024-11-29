package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class AvatarServiceValidatorTest {
    private final AvatarServiceValidator validator = new AvatarServiceValidator();

    @Test
    public void checkUserShouldThrowExceptionWhenUserIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.checkUser (null);
        });

        assertEquals("Пустой пользователь или его имя", exception.getMessage());
    }

    @Test
    public void checkUserShouldThrowExceptionWhenUsernameIsNull() {
        User user = new User();
        user.setUsername(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.checkUser (user);
        });

        assertEquals("Пустой пользователь или его имя", exception.getMessage());
    }

    @Test
    public void checkUserShouldThrowExceptionWhenUsernameIsBlank() {
        User user = new User();
        user.setUsername("  ");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.checkUser (user);
        });

        assertEquals("Пустой пользователь или его имя", exception.getMessage());
    }

    @Test
    public void checkUserShouldNotThrowExceptionWhenUserIsValid() {
        User user = new User();
        user.setUsername("validUsername");
        assertDoesNotThrow(() -> {
            validator.checkUser (user);
        });
    }
}