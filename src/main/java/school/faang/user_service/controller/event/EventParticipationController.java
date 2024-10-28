package school.faang.user_service.controller.event;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDTO;

import java.util.List;

@RequestMapping(value = EventParticipationController.REST_URL)
public interface EventParticipationController {
    String REST_URL = "/api/v1/events/{eventId}";

    @PutMapping(value = "/users/{userId}/register")
    ResponseEntity<Void> register(@PathVariable long eventId, @PathVariable long userId);

    @DeleteMapping(value = "/users/{userId}/unregister")
    ResponseEntity<Void> unregister(@PathVariable long eventId, @PathVariable long userId);

    @GetMapping(value = "/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<UserDTO>> findAllParticipantsByEventId(@PathVariable long eventId);

    @GetMapping(value = "/numberOfParticipants")
    ResponseEntity<Integer> countParticipants(@PathVariable long eventId);
}
