package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("create")
    public ResponseEntity<EventDto> create(@Valid EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventDto> getEvent(@NotNull @PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @DeleteMapping("{eventId}")
    public ResponseEntity<EventDto> deleteEvent(@NotNull @PathVariable Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @GetMapping("get-by-filters")
    public ResponseEntity<List<EventDto>> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @PutMapping
    public ResponseEntity<EventDto> updateEvent(@Valid EventDto event) {
        return eventService.updateEvent(event);
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@NotNull @PathVariable Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("user-participated-events/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@NotNull @PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}