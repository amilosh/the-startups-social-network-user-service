package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.participation.EventNotFoundException;
import school.faang.user_service.exception.participation.ParticipationException;
import school.faang.user_service.exception.participation.UserNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;

    public void validateUserExists(long userId) {
        log.info("Validating if user with ID {} exists", userId);
        if (!eventRepository.existsById(userId)) {
            log.error("Validation failed: User with ID {} does not exist", userId);
            throw new UserNotFoundException("User id " + userId + " does not exist");
        }
        log.info("Validation passed: User with ID {} exists", userId);
    }

    public void validateEventExists(long eventId) {
        log.info("Validating if event with ID {} exists", eventId);
        if (!eventRepository.existsById(eventId)) {
            log.error("Validation failed: Event with ID {} does not exist", eventId);
            throw new EventNotFoundException("Event with ID " + eventId + " does not exist");
        }
        log.info("Validation passed: Event with ID {} exists", eventId);
    }

    public void validateUserIsRegistered(long eventId, long userId) {
        log.info("Validating if user with ID {} is registered for event with ID {}", userId, eventId);
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            log.error("Validation failed: User with ID {} is not registered for event with ID {}", userId, eventId);
            throw new ParticipationException("User is not registered for this event.");
        }
        log.info("Validation passed: User with ID {} is registered for event with ID {}", userId, eventId);
    }

    public void validateUserNotRegistered(long eventId, long userId) {
        log.info("Validating if user with ID {} is not registered for event with ID {}", userId, eventId);
        if (eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            log.error("Validation failed: User with ID {} is already registered for event with ID {}", userId, eventId);
            throw new ParticipationException("User is already registered for this event.");
        }
        log.info("Validation passed: User with ID {} is not registered for event with ID {}", userId, eventId);
    }
}
