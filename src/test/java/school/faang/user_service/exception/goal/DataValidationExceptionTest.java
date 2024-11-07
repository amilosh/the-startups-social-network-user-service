package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataValidationExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "exception message";
        DataValidationException dataValidationException = new DataValidationException(message);

        assertEquals(message, dataValidationException.getMessage());
    }
}
