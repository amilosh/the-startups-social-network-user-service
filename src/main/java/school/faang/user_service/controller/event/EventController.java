package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
//@RestController
//@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    //    @PostMapping("/event/create")
    public EventDto create(EventDto event) {
        return eventService.create(event);
    }

    //    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    //    @GetMapping
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    //    @GetMapping("/{ownerId}")
    public List<EventDto> getOwnedEvents(@PathVariable Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    //    @GetMapping("/{participantId}")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }

}
