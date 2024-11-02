package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("api/v1/event-participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @GetMapping("/participants-list/{eventId}")
    public List<UserDto> findAllParticipantsByEventId(@PathVariable long eventId) {
        return eventParticipationService.findAllParticipantsByEventId(eventId);
    }

    @GetMapping("/participants-number/{eventId}")
    public int findParticipantsAmountByEventId(@PathVariable long eventId) {
        return eventParticipationService.findParticipantsAmountByEventId(eventId);
    }

    @PutMapping("/register/{eventId}/{userId}")
    public void registerParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    @PutMapping("/unregister/{eventId}/{userId}")
    public void unregisterParticipant(@PathVariable long eventId, @PathVariable long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }
}
