package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> register(long eventId, long userId) {
        try{
            log.info("New request to register user with id: {} for event with id: {}", userId, eventId);
            service.register(eventId, userId);
            log.info("User with id: {} was registered for event with id: {}", userId, eventId);

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            log.error("Error registering user {} for event {}: {}", userId, eventId, e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> unregister(long eventId, long userId) {
        try{
            log.info("New request to unregister user with id: {} from event with id: {}", userId, eventId);
            service.unregister(eventId, userId);
            log.info("User with id: {} was unregistered from event with id: {}", userId, eventId);

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e){
            log.error("Error unregistering user {} from event {}: {}", userId, eventId, e.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<UserDTO>> findAllParticipantsByEventId(long eventId) {
        log.info("New request to get all users from event with id: {}",  eventId);

        List<User> users = service.findAllParticipantsByEventId(eventId);

        return ResponseEntity.ok(mapper.toDTO(users));
    }

    @Override
    public ResponseEntity<Integer> countParticipants(long eventId) {
        return ResponseEntity.ok(service.countParticipants(eventId));
    }
}
