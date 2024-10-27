package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Tag(name = "Events")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        Event eventForCreate = eventMapper.toEvent(eventDto);
        return eventMapper.toDto(eventService.create(eventForCreate));
    }

    @PatchMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        return eventMapper.toDto(eventService.updateEvent(event));
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") Long eventId) {
        Event event = eventService.getEvent(eventId);
        return eventMapper.toDto(event);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        List<Event> events = eventService.getEventsByFilter(filter);
        return eventMapper.toDtoList(events);
    }

    @GetMapping("/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") Long userId) {
        List<Event> events = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(events);
    }

    @GetMapping("/participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") Long userId) {
        List<Event> events = eventService.getParticipatedEvents(userId);
        return eventMapper.toDtoList(events);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
