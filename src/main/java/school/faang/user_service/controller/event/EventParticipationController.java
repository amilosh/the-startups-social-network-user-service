package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
@Slf4j
public class EventParticipationController {
    private final EventParticipationService service;

    @PutMapping(value = "/events/{eventId}/users/{userId}/register")
    @ResponseStatus(value = HttpStatus.OK)
    public void register(@PathVariable long eventId, @PathVariable long userId) {
        try{
            log.info("New request to register user with id: {} for event with id: {}", userId, eventId);
            service.register(eventId, userId);
            log.info("User with id: {} was registered for event with id: {}", userId, eventId);

        } catch (IllegalArgumentException e) {
            log.error("Error registering user {} for event {}: {}", userId, eventId, e.getMessage());
        }
    }

    @DeleteMapping(value = "/events/{eventId}/users/{userId}/unregister")
    @ResponseStatus(value = HttpStatus.OK)
    public void unregister(@PathVariable long eventId, @PathVariable long userId) {
        try{
            log.info("New request to unregister user with id: {} from event with id: {}", userId, eventId);
            service.unregister(eventId, userId);
            log.info("User with id: {} was unregistered from event with id: {}", userId, eventId);

        } catch (IllegalArgumentException e){
            log.error("Error unregistering user {} from event {}: {}", userId, eventId, e.getMessage());
        }
    }

    @GetMapping(value = "/events/{eventId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public List<UserDTO> findAllParticipantsByEventId(@PathVariable long eventId) {
        log.info("New request to get all users from event with id: {}",  eventId);

        return service.findAllParticipantsByEventId(eventId);
    }

    @GetMapping(value = "/events/{eventId}/numberOfParticipants")
    @ResponseStatus(value = HttpStatus.OK)
    public Integer countParticipants(@PathVariable long eventId) {
        return service.countParticipants(eventId);
    }
}
