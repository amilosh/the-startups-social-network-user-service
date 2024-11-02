package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
@Slf4j
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@RequestBody @Valid EventDto eventDto) {
        log.info("Creating event: {}", eventDto);
        EventDto createdEvent = eventService.create(eventDto);
        log.info("Event created successfully: {}", createdEvent);
        return createdEvent;
    }

    @GetMapping("/event/{eventId}")
    public EventDto getEvent(@PathVariable @NotNull @Positive long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        return eventService.getEvent(eventId);
    }

    @GetMapping("/filteredEvents")
    public List<EventDto> getEventsByFilter(@RequestBody @NotNull EventFilterDto filters) {
        log.info("Fetching event's list with filters: {}", filters);
        return eventService.getEventsByFilter(filters);
    }

    @DeleteMapping("/event/delete/{eventId}")
    public void deleteEvent(@PathVariable @NotNull long eventId) {
        log.info("Deleting event with ID: {}", eventId);
        eventService.deleteEvent(eventId);
    }

    @PostMapping("/update")
    public EventDto updateEvent(@RequestBody @Valid EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/users/OwnedEvents/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable @NotNull long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/users/ParticipatedEvents/{userId}")
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}