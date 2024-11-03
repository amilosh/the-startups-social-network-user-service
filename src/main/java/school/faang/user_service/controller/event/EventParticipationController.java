package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    @PostMapping("/{eventId}/register")
    public ResponseEntity<Void> registerParticipant(@PathVariable @Min(1) long eventId,
                                              @RequestParam @Min(1) long userId) {
        service.registerParticipant(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable @Min(1) long eventId,
                                      @RequestParam @Min(1) long userId) {
        service.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/getAllParicipant")
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable @Min(1) long eventId) {
        return ResponseEntity.ok(service.getParticipant(eventId));
    }

    @PostMapping("/{eventId}/getParticipantsCount")
    public ResponseEntity<Integer> getParticipantsCount(@PathVariable @Min(1) long eventId) {
        return ResponseEntity.ok(service.getParticipantsCount(eventId));
    }

}
