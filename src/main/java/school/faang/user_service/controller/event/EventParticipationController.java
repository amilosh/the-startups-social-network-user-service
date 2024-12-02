package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/event/participation")
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/{userId}")
    public ResponseEntity<Void> registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        try {
            eventParticipationService.registerParticipant(eventId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{eventId}/{userId}")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        try {
            eventParticipationService.unregisterParticipant(eventId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable long eventId) {
        try {
            return ResponseEntity.ok(eventParticipationService.getParticipant(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count/{eventId}")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable long eventId) {
        try {
            return ResponseEntity.ok(eventParticipationService.getParticipantsCount(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
