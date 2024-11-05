package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.UserAlreadyRegisteredException;
import school.faang.user_service.exceptions.UserNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.validation.EventParticipationServiceValidator;

import java.util.List;

import static school.faang.user_service.validation.EventParticipationServiceValidator.validateEventId;
import static school.faang.user_service.validation.EventParticipationServiceValidator.validateUserId;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private EventParticipationServiceValidator validator;

    private boolean userExists(long eventId, long userId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void registerParticipant(long eventId, long userId) {

        validateUserId(userId);
        validateEventId(eventId);

        if (userExists(eventId, userId)) {
            log.error("User already registered: eventId={}, userId={}", eventId, userId);
            throw new UserAlreadyRegisteredException("User is already registered for the event");
        }

        eventParticipationRepository.register(eventId, userId);
        log.info("User registered: eventId={}, userId={}", eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {

        validateUserId(userId);
        validateEventId(eventId);

        if (!userExists(eventId, userId)) {
            log.warn("User not found: eventId={}, userId={}", eventId, userId);
            throw new UserNotFoundException("User is not registered for the event");
        }

        eventParticipationRepository.unregister(eventId, userId);
        log.info("User unregistered: eventId={}, userId={}", eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        validateEventId(eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        validateEventId(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}
