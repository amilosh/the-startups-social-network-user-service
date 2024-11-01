package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.service.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/events")
@Slf4j
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create (@RequestBody @Valid EventDto eventDto) {
        log.info("Creating event: {}", eventDto);
        EventDto createdEvent = eventService.create(eventDto);
        log.info("Event created successfully: {}", createdEvent);
        return createdEvent;
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@RequestParam @NotNull @Positive Long eventId){
        log.info("Fetching event with ID: {}", eventId);
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(@NotNull EventFilterDto filters){
        log.info("Fetching event's list with filters: {}", filters);
        return eventService.getEventsByFilter(filters);
    }
}
