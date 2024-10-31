package school.faang.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.service.EventService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/events")
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@Validated EventDto eventDto) {
        log.info("Creating event: {}", eventDto);
        EventDto createdEvent = eventService.create(eventDto);
        log.info("Event created successfully: {}", createdEvent);
        return createdEvent;
    }
}
