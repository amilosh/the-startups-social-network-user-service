package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    @Operation(summary = "Register a user as a participant in an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered as a participant"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/{eventId}/register")
    public ResponseEntity<Void> registerParticipant(@PathVariable @Min(1) long eventId,
                                                    @RequestParam @Min(1) long userId) {
        service.registerParticipant(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Unregister a user from an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully unregistered from the event"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable @Min(1) long eventId,
                                                      @RequestParam @Min(1) long userId) {
        service.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get participants of a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participants found successfully")
    })
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<UserDto>> getParticipant(@PathVariable @Min(1) long eventId) {
        return ResponseEntity.ok(service.getParticipant(eventId));
    }

    @Operation(summary = "Get the count of participants in a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participants count retrieved successfully")
    })
    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Integer> getParticipantsCount(@PathVariable @Min(1) long eventId) {
        return ResponseEntity.ok(service.getParticipantsCount(eventId));
    }
}
