package school.faang.user_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataValidationExceptionTest {
    DataValidationException dataValidationException;

    @Test
    void testExceptionMessage() {
        String message = "Exception message";
        dataValidationException = new DataValidationException(message);

        assertEquals(message, dataValidationException.getMessage());
    }
}
