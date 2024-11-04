package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaxGoalsPerUserExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "exception message";
        MaxGoalsPerUserException maxGoalsPerUserException = new MaxGoalsPerUserException(message);

        assertEquals(message, maxGoalsPerUserException.getMessage());
    }
}
