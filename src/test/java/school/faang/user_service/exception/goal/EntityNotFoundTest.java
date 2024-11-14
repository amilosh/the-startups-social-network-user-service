package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityNotFoundTest {

    @Test
    @DisplayName("Test Exception Message")
    void testExceptionMessage() {
        String message = "exception message";
        EntityNotFound entityNotFound = new EntityNotFound(message);

        assertEquals(message, entityNotFound.getMessage());
    }
}
