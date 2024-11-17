package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.partiсipation.EventNotFoundException;
import school.faang.user_service.exception.partiсipation.ParticipationException;
import school.faang.user_service.exception.partiсipation.UserNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;

    public void validateUserExists (long userId) {
        if (!eventRepository.existsById(userId)) {
            throw new UserNotFoundException("User id" + userId + " does not exist");
        }
    }

    public void validateEventExists(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with ID " + eventId + " does not exist");
        }
    }

    public void validateUserIsRegistered(long eventId, long userId) {
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ParticipationException("User is not registered for this event.");
        }
    }

    public void validateUserNotRegistered(long eventId, long userId) {
        if (eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ParticipationException("User is already registered for this event.");
        }
    }
}
