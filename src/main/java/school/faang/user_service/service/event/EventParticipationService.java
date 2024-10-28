package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {
    private final EventParticipationRepository repository;

    @Transactional
    public void register(long eventId, long userId){
        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (alreadyRegistered) {
            log.error("User already registered with id: {}", userId);
            throw new IllegalArgumentException("User already registered");
        }

        repository.register(eventId, userId);
    }

    @Transactional
    public void unregister(long eventId, long userId){
        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (!alreadyRegistered) {
            log.error("User isn't registered: {}", userId);
            throw new IllegalArgumentException("User isn't registered");
        }

        repository.unregister(eventId, userId);
    }

    @Transactional
    public List<User> findAllParticipantsByEventId(long eventId){
        if (eventId == 0) {
            throw new IllegalArgumentException("Event id can't be zero.");
        }

        return repository.findAllParticipantsByEventId(eventId);
    }

    public int countParticipants(long eventId){
        if (eventId == 0) {
            throw new IllegalArgumentException("Event id can't be zero.");
        }

        return repository.countParticipants(eventId);
    }
}
