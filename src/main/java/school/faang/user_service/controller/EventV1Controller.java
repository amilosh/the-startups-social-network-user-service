package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Slf4j
@Validated
public class EventV1Controller {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@RequestBody @Valid EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @GetMapping("{eventId}")
    public EventDto getEvent(@PathVariable @NotNull @Positive long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filteredEvents")
    public List<EventDto> getEventsByFilter(@RequestBody @NotNull EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("{eventId}")
    public void deleteEvent(@PathVariable @NotNull long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping
    public EventDto updateEvent(@RequestBody @Valid EventDto eventDto) {
       if(eventDto.id() == null){
           throw new DataValidationException("Do not update non-existent event");
       }
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