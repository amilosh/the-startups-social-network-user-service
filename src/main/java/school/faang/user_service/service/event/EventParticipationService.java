package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ParticipantRegistrationException;
import school.faang.user_service.mapper.UserDTOMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {
    private final EventParticipationRepository repository;
    private final UserDTOMapper mapper;

    @Transactional
    public void register(Long eventId, Long userId){
        validateIdsNotNull(eventId, userId);

        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (alreadyRegistered) {
            log.error("User already registered with id: {}", userId);
            throw new ParticipantRegistrationException("User already registered");
        }

        repository.register(eventId, userId);
    }

    @Transactional
    public void unregister(Long eventId, Long userId){
        validateIdsNotNull(eventId, userId);

        boolean alreadyRegistered = repository.existsByEventIdAndUserId(eventId, userId);

        if (!alreadyRegistered) {
            log.error("User isn't registered: {}", userId);
            throw new ParticipantRegistrationException("User isn't registered");
        }

        repository.unregister(eventId, userId);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAllParticipantsByEventId(Long eventId){
        if (eventId == null || eventId == 0) {
            throw new IllegalArgumentException("Event id can't be zero.");
        }

        List<User> users = repository.findAllParticipantsByEventId(eventId);

        return mapper.toDTO(users);
    }
    @Transactional(readOnly = true)
    public int countParticipants(Long eventId){
        if (eventId == null || eventId == 0) {
            throw new IllegalArgumentException("Event id can't be zero.");
        }

        return repository.countParticipants(eventId);
    }

    private void validateIdsNotNull(Long eventId, Long userId) {
        if (eventId == null || eventId == 0) {
            log.error("Event ID is null");
            throw new IllegalArgumentException("Event ID must not be null");
        }
        if (userId == null || userId == 0) {
            log.error("User ID is null");
            throw new IllegalArgumentException("User ID must not be null");
        }
    }
}
