package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event-participations")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerParticipation(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId,
            @RequestParam @NotNull(message = "User ID should not be null") Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return "The user has successfully registered for the event";
    }

    @DeleteMapping("/{eventId}/unregister")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterParticipant(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId,
            @RequestParam @NotNull(message = "User ID should not be null") Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @GetMapping("/{eventId}/participants")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getParticipants(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    @ResponseStatus(HttpStatus.OK)
    public int getParticipantsCount(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}

