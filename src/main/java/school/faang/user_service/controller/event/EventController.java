package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/{eventId}")
    public EventDto get(@PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        return eventService.get(eventId);
    }

    @GetMapping
    public List<EventDto> getByFilter(@Valid @ModelAttribute EventFilterDto filter) {
        return eventService.getByFilter(filter);
    }

    @DeleteMapping("/{eventId}")
    public void delete(@PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        eventService.delete(eventId);
    }

    @PutMapping
    public EventDto update(@Valid @RequestBody EventDto event) {
        return eventService.update(event);
    }

    @GetMapping("/users/{userId}/owned-events")
    public List<EventDto> getOwnedEvents(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/users/{userId}/participated-events")
    public List<EventDto> getParticipatedEvents(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
