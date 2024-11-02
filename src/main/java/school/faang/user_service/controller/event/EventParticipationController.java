package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    @PostMapping("/{eventId}/register")
    public void registerParticipant(@PathVariable @Min(1) long eventId,
                                    @RequestParam @Min(1) long userId) {
        service.registerParticipant(eventId, userId);
    }

    @PostMapping("/{eventId}/unregister")
    public void unregisterParticipant(@PathVariable @Min(1) long eventId,
                                      @RequestParam @Min(1) long userId) {
        service.unregisterParticipant(eventId, userId);
    }

    @PostMapping("/{eventId}/getAllParicipant")
    public List<User> getParticipant(@PathVariable @Min(1) long eventId) {
        return service.getParticipant(eventId);
    }

    @PostMapping("/{eventId}/getParticipantsCount")
    public int getParticipantsCount(@PathVariable @Min(1) long eventId) {
        return service.getParticipantsCount(eventId);
    }

}
