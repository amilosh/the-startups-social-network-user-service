package school.faang.user_service.exception.goal;

import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.NoSuchElementException;

public class InvitationEntityNotFoundException extends NoSuchElementException {
    public InvitationEntityNotFoundException(String message) {
        super(String.format(message));
    }

    public InvitationEntityNotFoundException(String message, List<Goal> goals) {


    }
}
