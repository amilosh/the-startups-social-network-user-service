package school.faang.user_service.controller.event;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.UserDTO;

import java.util.List;

@RequestMapping(value = EventParticipationController.REST_URL)
public interface EventParticipationController {
    String REST_URL = "/api/v1/events/{eventId}";

    @PostMapping(value = "/users/{userId}/register")
    void register(@PathVariable long eventId, @PathVariable long userId);

    @PostMapping(value = "/users/{userId}/unregister")
    void unregister(@PathVariable long eventId, @PathVariable long userId);

    @GetMapping(value = "/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    List<UserDTO> findAllParticipantsByEventId(@PathVariable long eventId);

    @GetMapping(value = "/numberOfParticipants")
    int countParticipants(@PathVariable long eventId);
}
