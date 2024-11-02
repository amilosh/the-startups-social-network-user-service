package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.UserAlreadyRegisteredException;
import school.faang.user_service.exceptions.UserNotFoundException;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@Slf4j
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventParticipationService(EventParticipationRepository eventParticipationRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
    }

    private boolean userExists(long eventId, long userId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void registerParticipant(long eventId, long userId) {

        if (userExists(eventId, userId)) {
            log.warn("User already registered: eventId={}, userId={}", eventId, userId);
            throw new UserAlreadyRegisteredException("User is already registered for the event");
        }

        eventParticipationRepository.register(eventId, userId);
        log.info("User registered: eventId={}, userId={}", eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {

        if (!userExists(eventId, userId)) {
            log.warn("User not found: eventId={}, userId={}", eventId, userId);
            throw new UserNotFoundException("User is not registered for the event");
        }

        eventParticipationRepository.unregister(eventId, userId);
        log.info("User unregistered: eventId={}, userId={}", eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
