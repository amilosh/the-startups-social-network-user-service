package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserDTOMapper;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventParticipationControllerImpl implements EventParticipationController {
    private final UserDTOMapper mapper;
    private final EventParticipationService service;

    @Override
    public void register(long eventId, long userId) {
        log.info("New request to register user with id: {} for event with id: {}", userId, eventId);

        service.register(eventId, userId);

        log.info("User with id: {} was registered for event with id: {}", userId, eventId);
    }

    @Override
    public void unregister(long eventId, long userId) {
        log.info("New request to unregister user with id: {} from event with id: {}", userId, eventId);

        service.unregister(eventId, userId);

        log.info("User with id: {} was unregistered from event with id: {}", userId, eventId);
    }

    @Override
    public List<UserDTO> findAllParticipantsByEventId(long eventId) {
        log.info("New request to get all users from event with id: {}",  eventId);

        List<User> users = service.findAllParticipantsByEventId(eventId);

        return mapper.toDTO(users);
    }

    @Override
    public int countParticipants(long eventId) {
        return service.countParticipants(eventId);
    }
}
