package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserDTOMapper;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@Slf4j
public class EventParticipationController {
    private final UserDTOMapper mapper;
    private final EventParticipationService service;

    @PutMapping(value = "/events/{eventId}/users/{userId}/register")
    public ResponseEntity<Void> register(@PathVariable long eventId, @PathVariable long userId) {
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

    @DeleteMapping(value = "/events/{eventId}/users/{userId}/unregister")
    public ResponseEntity<Void> unregister(@PathVariable long eventId, @PathVariable long userId) {
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

    @GetMapping(value = "/events/{eventId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> findAllParticipantsByEventId(@PathVariable long eventId) {
        log.info("New request to get all users from event with id: {}",  eventId);

        List<User> users = service.findAllParticipantsByEventId(eventId);

        return ResponseEntity.ok(mapper.toDTO(users));
    }

    @GetMapping(value = "/events/{eventId}/numberOfParticipants")
    public ResponseEntity<Integer> countParticipants(@PathVariable long eventId) {
        return ResponseEntity.ok(service.countParticipants(eventId));
    }
}
