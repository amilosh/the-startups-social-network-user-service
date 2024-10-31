package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository repository;

    public void registerParticipant(long eventId, long userId) {
        validateRegistration(eventId, userId);
        repository.register(eventId, userId);
    }

    private void validateRegistration(long eventId, long userId) {
        if (repository.findAllParticipantsByEventId(eventId).stream().
                anyMatch(user -> user.getId() == userId)) {
            throw new IllegalStateException("User is already registered to event");
        }
    }

}
