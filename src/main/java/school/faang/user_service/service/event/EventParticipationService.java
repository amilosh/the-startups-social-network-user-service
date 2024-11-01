package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
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

    private boolean userExists(long eventId, long userId){
        return eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void registerParticipant(long eventId, long userId) {
        if (!userExists(eventId, userId)) {
            eventParticipationRepository.register(eventId, userId);
            log.info("User registered");
        } else {
            log.info("User exists");
        }
    }

    public void unregisterParticipant(long eventId, long userId){
        if (userExists(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("User unregistered");
        } else {
            log.info("User not found");
        }
    }

    public List<User> getParticipant(long eventId){
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId){
        return eventParticipationRepository.countParticipants(eventId);
    }
}
