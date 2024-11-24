package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
@Tag(name = "Event Controller", description = "Controller for managing events")
@ApiResponse(responseCode = "200", description = "Event retrieved successfully")
@ApiResponse(responseCode = "201", description = "Event created successfully")
@ApiResponse(responseCode = "204", description = "Event deleted successfully")
@ApiResponse(responseCode = "400", description = "Invalid request body")
@ApiResponse(responseCode = "404", description = "Event not found")
@ApiResponse(responseCode = "500", description = "Server error")
public class EventController {

    private final EventService eventService;

    @Operation(
            summary = "Create an event",
            description = "Create a new event with the provided details"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody EventDto event) {
        return eventService.create(event);
    }

    @Operation(
            summary = "Get an event by ID",
            description = "Retrieve details of an event using its ID"
    )
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto get(@PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        return eventService.get(eventId);
    }

    @Operation(
            summary = "Get events by filter",
            description = "Retrieve a list of events based on the specified filter criteria"
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getByFilter(@Valid @ModelAttribute EventFilterDto filter) {
        return eventService.getByFilter(filter);
    }

    @Operation(
            summary = "Delete an event",
            description = "Delete an event using its ID"
    )
    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        eventService.delete(eventId);
    }

    @Operation(
            summary = "Update an event",
            description = "Update an existing event with new details"
    )
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public EventDto update(@Valid @RequestBody EventDto event) {
        return eventService.update(event);
    }

    @Operation(
            summary = "Get user's owned events",
            description = "Retrieve a list of events owned by a specific user"
    )
    @GetMapping("/users/{userId}/owned-events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getOwnedEvents(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @Operation(
            summary = "Get user's participated events",
            description = "Retrieve a list of events the user has participated in"
    )
    @GetMapping("/users/{userId}/participated-events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getParticipatedEvents(@PathVariable @NotNull(message = "User ID should not be null") Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
