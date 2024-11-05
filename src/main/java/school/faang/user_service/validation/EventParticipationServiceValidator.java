package school.faang.user_service.validation;

import org.springframework.stereotype.Component;

@Component
public class EventParticipationServiceValidator {

    public static void validateUserId(long userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("userId cannot be negative:" +
                    " userId=" + userId);
        }
    }

    public static void validateEventId(long eventId) {
        if (eventId < 0) {
            throw new IllegalArgumentException("eventId cannot be negative:" +
                    " eventId=" + eventId);
        }
    }
}
