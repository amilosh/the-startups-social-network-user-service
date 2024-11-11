package school.faang.user_service.service.validator.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.ParticipationException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;

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

    public void validateParticipantsCount(long eventId) {
        int participantsCount = eventParticipationRepository.countParticipants(eventId);
        if (participantsCount == 0) {
            throw new EventNotFoundException("No participants found for the event with ID " + eventId);
        }
    }
}
