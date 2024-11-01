package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventParticipationService;

@Component
public class EventParticipationController {
    private final EventParticipationService eventParticipationService;

    @Autowired
    EventParticipationController(EventParticipationService eventParticipationService){
        this.eventParticipationService = eventParticipationService;
    }

}
