package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.utilities.UrlUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.EVENTS)
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @PutMapping(UrlUtils.ID + UrlUtils.REGISTER)
    public ResponseEntity<Void> registerParticipant(@PathVariable("id") Long eventId, @RequestParam("userId") Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(UrlUtils.ID + UrlUtils.UNREGISTER)
    public ResponseEntity<Void> unregisterParticipant(@PathVariable("id") Long eventId, @RequestParam("userId") Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(UrlUtils.ID + UrlUtils.PARTICIPANTS)
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable("id") Long eventId) {
        return ResponseEntity.ok(eventParticipationService.getParticipant(eventId));
    }

    @GetMapping(UrlUtils.ID + UrlUtils.PARTICIPANTS + UrlUtils.AMOUNT)
    public ResponseEntity<Long> getParticipantsCount(@PathVariable("id") Long eventId) {
        return ResponseEntity.ok(eventParticipationService.getParticipantsCount(eventId));
    }
}
