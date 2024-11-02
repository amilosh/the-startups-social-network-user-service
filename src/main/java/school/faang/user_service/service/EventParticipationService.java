package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;

    public void registerParticipant(long eventId, long userId) {
        if (checkRegistration(eventId, userId)) {
            throw new IllegalStateException("User is already registered to event");
        }
        repository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!checkRegistration(eventId, userId)) {
            throw new IllegalStateException("User is not registered to event");
        }
        repository.unregister(eventId, userId);
    }

    private boolean checkRegistration(long eventId, long userId) {
        return repository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public List<User> getParticipant(long eventId) {
        checkEventById(eventId);
        return repository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        checkEventById(eventId);
        return repository.countParticipants(eventId);
    }

    private void checkEventById(long eventId) {
        if (repository.findAllParticipantsByEventId(eventId) == null) {
            throw new IllegalStateException("There is no event with this id");
        }
    }

}
