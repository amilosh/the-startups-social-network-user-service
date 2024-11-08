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
import school.faang.user_service.entity.User;
import school.faang.user_service.service.EventParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/registerParticipant")
    public String registerParticipation(@RequestParam Long eventId, @RequestParam Long userId) {
        try {
            eventParticipationService.registerParticipant(eventId, userId);
            return "Пользователь успешно зарегистрирован на событие";
        } catch (Exception e) {
            return "Ошибка регистрации: " + e.getMessage();
        }
    }

    @DeleteMapping("/{eventId}/unregister/{userId}")
    public String unregisterParticipant(@PathVariable Long eventId, @PathVariable Long userId) {
        try {
            eventParticipationService.unregisterParticipant(eventId, userId);
            return "Пользователь отписан от события";
        } catch (Exception e) {
            return "Ошибка отписки: " + e.getMessage();
        }

    }

    @GetMapping("/{eventId}/participants")
    public List<User> getParticipants(@PathVariable Long eventId) {
        try {
            return eventParticipationService.getParticipant(eventId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/{eventId}/participants/count")
    public int getParticipantsCount(@PathVariable Long eventId) {
        try {
            return eventParticipationService.getParticipantsCount(eventId);
        } catch (Exception e) {
            return 0;
        }
    }
}