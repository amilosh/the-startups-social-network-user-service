package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    public void registerParticipant(long eventId, long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    public List<User> getParticipant(long eventId, long userId) {
        return eventParticipationService.getParticipant(eventId);
    }

    public int getParticipantsCount(long eventId, long userId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }

}
