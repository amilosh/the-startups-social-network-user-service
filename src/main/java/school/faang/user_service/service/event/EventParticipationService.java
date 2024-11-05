package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        if (isRegisteredParticipant(eventId, userId)) {
           throw new IllegalArgumentException("User is already participating");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (!isRegisteredParticipant(eventId, userId)) {
            throw new IllegalArgumentException("User is not participating");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    public boolean isRegisteredParticipant(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream().anyMatch(user -> user.getId() == userId);
    }

    public List<UserDto> getParticipant (long eventId) {
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantsCount(long eventId){
        return eventParticipationRepository.countParticipants(eventId);
    }
}