package school.faang.user_service.validator.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {

    private final UserValidator userValidator = new UserValidator();

    @Test
    public void validateUserExistenceThrowExceptionTest() {
        boolean isExist = false;

        assertThrows(EntityNotFoundException.class,
                () -> userValidator.validateUserExistence(isExist));
    }

    @Test
    public void validateUserExistenceDoesNotThrowExceptionTest() {
        boolean isExist = true;

        assertDoesNotThrow(() -> userValidator.validateUserExistence(isExist));
    }
}

