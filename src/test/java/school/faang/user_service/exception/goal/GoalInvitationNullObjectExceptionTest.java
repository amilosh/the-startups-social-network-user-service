package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalInvitationNullObjectExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "exception message";
        GoalInvitationException goalInvitationException = new GoalInvitationException(message);

        assertEquals(message, goalInvitationException.getMessage());
    }
}
