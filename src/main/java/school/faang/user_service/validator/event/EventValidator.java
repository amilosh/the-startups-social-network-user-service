package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.partiсipation.EventNotFoundException;
import school.faang.user_service.exception.partiсipation.ParticipationException;
import school.faang.user_service.exception.partiсipation.UserNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;

    private static final Logger logger = Logger.getLogger(EventValidator.class.getName());

    public void validateUserExists(long userId) {
        logger.info("Validating if user with ID " + userId + " exists");
        if (!eventRepository.existsById(userId)) {
            logger.log(Level.SEVERE, "Validation failed: User with ID " + userId + " does not exist");
            throw new UserNotFoundException("User id " + userId + " does not exist");
        }
        logger.info("Validation passed: User with ID " + userId + " exists");
    }

    public void validateEventExists(long eventId) {
        logger.info("Validating if event with ID " + eventId + " exists");
        if (!eventRepository.existsById(eventId)) {
            logger.log(Level.SEVERE, "Validation failed: Event with ID " + eventId + " does not exist");
            throw new EventNotFoundException("Event with ID " + eventId + " does not exist");
        }
        logger.info("Validation passed: Event with ID " + eventId + " exists");
    }

    public void validateUserIsRegistered(long eventId, long userId) {
        logger.info("Validating if user with ID " + userId + " is registered for event with ID " + eventId);
        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            logger.log(Level.SEVERE, "Validation failed: User with ID " + userId + " is not registered for event with ID " + eventId);
            throw new ParticipationException("User is not registered for this event.");
        }
        logger.info("Validation passed: User with ID " + userId + " is registered for event with ID " + eventId);
    }

    public void validateUserNotRegistered(long eventId, long userId) {
        logger.info("Validating if user with ID " + userId + " is not registered for event with ID " + eventId);
        if (eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            logger.log(Level.SEVERE, "Validation failed: User with ID " + userId + " is already registered for event with ID " + eventId);
            throw new ParticipationException("User is already registered for this event.");
        }
        logger.info("Validation passed: User with ID " + userId + " is not registered for event with ID " + eventId);
    }
}