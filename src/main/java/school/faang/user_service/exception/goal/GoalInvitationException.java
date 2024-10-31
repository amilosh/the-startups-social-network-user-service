package school.faang.user_service.exception.goal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoalInvitationException extends RuntimeException {
    public GoalInvitationException(String message) {
        super(message);
        log.error(message);
    }
}
