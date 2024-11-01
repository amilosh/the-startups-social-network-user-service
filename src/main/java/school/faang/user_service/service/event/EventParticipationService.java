package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@Slf4j
public class EventParticipationService {
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    public EventParticipationService(EventParticipationRepository eventParticipationRepository) {
        this.eventParticipationRepository = eventParticipationRepository;
    }

    public void registerParticipant(long eventId, long userId) {
        boolean userExists = eventParticipationRepository
                .findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);

        if (userExists) {
            log.info("Пользователь зарегистрирован");
            eventParticipationRepository.register(eventId, userId);
        } else {
            log.info("Пользователь существует");
        }
    }

}
