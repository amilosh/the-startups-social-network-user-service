package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event-participations")
@Tag(name = "Event Participation Controller", description = "Controller for managing event participations")
@ApiResponse(responseCode = "201", description = "The user has successfully registered for the event")
@ApiResponse(responseCode = "204", description = "The user has been successfully unregistered from the event")
@ApiResponse(responseCode = "400", description = "Invalid input parameters")
@ApiResponse(responseCode = "404", description = "Event or user not found")
@ApiResponse(responseCode = "500", description = "Server error")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @Operation(
            summary = "Register a user for an event",
            description = "Register a user for a specific event using event ID and user ID"
    )
    @PostMapping("/{eventId}/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerParticipation(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId,
            @RequestParam @NotNull(message = "User ID should not be null") Long userId) {
        eventParticipationService.registerParticipant(eventId, userId);
        return "The user has successfully registered for the event";
    }

    @Operation(
            summary = "Unregister a user from an event",
            description = "Remove a user from an event using event ID and user ID"
    )
    @DeleteMapping("/{eventId}/unregister")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterParticipant(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId,
            @RequestParam @NotNull(message = "User ID should not be null") Long userId) {
        eventParticipationService.unregisterParticipant(eventId, userId);
    }

    @Operation(
            summary = "Get participants of an event",
            description = "Retrieve a list of users who are registered for a specific event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Participants retrieved successfully")
            }
    )
    @GetMapping("/{eventId}/participants")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getParticipants(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        return eventParticipationService.getParticipants(eventId);
    }

    @Operation(
            summary = "Get participant count of an event",
            description = "Retrieve the count of users who are registered for a specific event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Participant count retrieved successfully")
            }
    )
    @GetMapping("/{eventId}/participants/count")
    @ResponseStatus(HttpStatus.OK)
    public int getParticipantsCount(
            @PathVariable @NotNull(message = "Event ID should not be null") Long eventId) {
        return eventParticipationService.getParticipantsCount(eventId);
    }
}
