package school.faang.user_service.service.Participation.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.validator.event.EventValidator;

import java.util.List;

@ControllerAdvice
@Component
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final EventValidator eventValidator;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        eventValidator.validateEventExists(eventId);
        eventValidator.validateUserNotRegistered(eventId, userId);
        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventValidator.validateEventExists(eventId);
        eventValidator.validateUserIsRegistered(eventId, userId);
        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<User> getParticipant(long eventId) {
        eventValidator.validateEventExists(eventId);
        return eventParticipationRepository.findUsersByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        eventValidator.validateEventExists(eventId);
        eventValidator.validateParticipantsCount(eventId);
        return eventParticipationRepository.countParticipants(eventId);
    }
}
