package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    public ResponseEntity<Void> registerParticipant(long eventId, long userId) {
        try {
            eventParticipationService.registerParticipant(eventId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Void> unregisterParticipant(long eventId, long userId) {
        try {
            eventParticipationService.unregisterParticipant(eventId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    public ResponseEntity<List<UserDto>> getParticipant(long eventId) {
        try {
            return ResponseEntity.ok(eventParticipationService.getParticipant(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Integer> getParticipantCount(long eventId) {
        try {
            return ResponseEntity.ok(eventParticipationService.getParticipantsCount(eventId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
