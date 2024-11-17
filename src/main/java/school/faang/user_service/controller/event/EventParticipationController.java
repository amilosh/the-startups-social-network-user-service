package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.participation.event.EventParticipationService;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/{eventId}/register")
    public String registerParticipation(@PathVariable Long eventId, @RequestParam Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return "The user has successfully registered for the event";
    }

    @DeleteMapping("/{eventId}/unregister")
    public String unregisterParticipant(@PathVariable Long eventId, @RequestParam Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
        return "The user has successfully unregistered for the event";
    }

    @GetMapping("/{eventId}/participants")
    public List<UserDto> getParticipants(@PathVariable Long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @GetMapping("/{eventId}/participants/count")
    public int getParticipantsCount(@PathVariable Long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);

    }
}