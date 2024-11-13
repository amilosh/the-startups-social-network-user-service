package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Tag(name = "Events")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @Operation(summary = "Update an existing event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PatchMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @Operation(summary = "Get event details by event ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") Long eventId) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "Get events by filter criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered events found successfully")
    })
    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @Operation(summary = "Get events owned by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Owned events found successfully")
    })
    @GetMapping("/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @Operation(summary = "Get events participated by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participated events found successfully")
    })
    @GetMapping("/participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @Operation(summary = "Delete an event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
