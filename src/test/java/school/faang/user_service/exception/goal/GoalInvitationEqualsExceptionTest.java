package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalInvitationEqualsExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "exception message";
        GoalInvitationEqualsException goalInvitationEqualsException = new GoalInvitationEqualsException(message);

        assertEquals(message, goalInvitationEqualsException.getMessage());
    }
}
