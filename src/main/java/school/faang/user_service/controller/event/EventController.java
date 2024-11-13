package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/event/create")
    public EventDto create(EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @GetMapping("/{ownerId}")
    public List<EventDto> getOwnedEvents(@PathVariable Long ownerId) {
        return eventService.getOwnedEvents(ownerId);
    }

    @GetMapping("/{participantId}")
    public List<EventDto> getParticipatedEvents(@PathVariable long participantId) {
        return eventService.getParticipatedEvents(participantId);
    }

}
