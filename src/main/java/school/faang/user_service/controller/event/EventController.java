package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public ResponseEntity<EventDto> create(@Valid EventDto eventDto) {
        return eventService.create(eventDto);
    }

    public ResponseEntity<EventDto> getEvent(@NotNull Long eventId) {
        return eventService.getEvent(eventId);
    }

    public ResponseEntity<EventDto> deleteEvent(@NotNull Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    public ResponseEntity<List<EventDto>> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public ResponseEntity<EventDto> updateEvent(@Valid EventDto event) {
        return eventService.updateEvent(event);
    }

    public ResponseEntity<List<EventDto>> getOwnedEvents(@NotNull Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public ResponseEntity<List<EventDto>> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}