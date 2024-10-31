package school.faang.user_service.controller.event;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(@Validated EventDto eventDto) {
        log.info("Creating event: {}", eventDto);
        EventDto createdEvent = eventService.create(eventDto);
        log.info("Event created successfully: {}", createdEvent);
        return createdEvent;
    }
}
