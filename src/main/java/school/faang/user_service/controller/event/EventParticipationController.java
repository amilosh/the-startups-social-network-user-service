package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.eventService.EventParticipationService;
import school.faang.user_service.service.EventParticipationValidator;


@RestController
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;
    private final EventParticipationValidator validator;

    @PostMapping("/events/register")
    public void registerParticipation(@RequestParam Long eventId, @RequestParam Long userId) {
        validator.validateParticipation(eventId, userId);
        eventParticipationService.registerParticipation(eventId, userId);
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public ResponseEntity<String> unregisterParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        eventParticipationService.unregisterParticipation(eventId, userId);
        return ResponseEntity.ok("Пользователь отписан от события.");
    }

}