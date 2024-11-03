package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS + UrlUtils.ID + UrlUtils.PARTICIPANTS)
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PostMapping()
    public ResponseEntity<Void> registerParticipant(@PathVariable("id") @Min(1) Long eventId, @RequestParam("userId") @Min(1) Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> unregisterParticipant(@PathVariable("id") @Min(1) Long eventId, @RequestParam("userId") @Min(1) Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable("id") @Min(1) Long eventId) {
        return ResponseEntity.ok(eventParticipationService.getParticipant(eventId));
    }

    @GetMapping(UrlUtils.AMOUNT)
    public ResponseEntity<Long> getParticipantsCount(@PathVariable("id") @Min(1) Long eventId) {
        return ResponseEntity.ok(eventParticipationService.getParticipantsCount(eventId));
    }
}
