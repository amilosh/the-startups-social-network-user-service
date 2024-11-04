package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.eventService.EventParticipationService;
import school.faang.user_service.service.EventParticipationValidator;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;
    private final EventParticipationValidator validator;

    @PostMapping("/register")
    public ResponseEntity<String> registerParticipation(@RequestParam Long eventId, @RequestParam Long userId) {
        validator.validateParticipation(eventId, userId);
        eventParticipationService.registerParticipation(eventId, userId);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован на событие");
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public ResponseEntity<String> unregisterParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        eventParticipationService.unregisterParticipation(eventId, userId);
        return ResponseEntity.ok("Пользователь отписан от события.");
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity <List<UserDto>> getParticipants (@PathVariable Long eventId) {
        List<UserDto> participants = eventParticipationService.getListOfParticipants(eventId);
        return ResponseEntity.ok(participants);
    }
    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Integer> getParticipantsCount(@PathVariable Long eventId) {
        int count = eventParticipationService.getParticipantsCount(eventId);
        return ResponseEntity.ok(count);
    }
}