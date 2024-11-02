package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoalInvitationExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "exception message";
        GoalInvitationNullObjectException goalInvitationNullObjectException = new GoalInvitationNullObjectException(message);

        assertEquals(message, goalInvitationNullObjectException.getMessage());
    }
}
