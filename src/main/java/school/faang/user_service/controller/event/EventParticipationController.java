package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/events/register")
    public void registerParticipation(@RequestParam Long eventId, @RequestParam Long userId) {
        validate(eventId, userId);
        eventParticipationService.registerParticipation(eventId, userId);
    }

    private void validate(Long eventId, Long userId) {
        if (eventId == null || userId == null) {
            throw new IllegalArgumentException("Event ID и User ID не могут быть null");
        }
    }
}