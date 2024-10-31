package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.EventValidation;


@Controller
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;
    private final EventValidation eventValidation;

    public EventDto create(EventDto event) {
        eventValidation.validateEvent(event);
        return eventService.create(event);
    }
}
