package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Tag(name = "MentorshipRequests", description = "API for managing mentorship requests.")
@RestController
@RequestMapping("/mentorships/requests")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @Operation(summary = "Create a mentorship request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mentorship request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@Valid @RequestBody MentorshipRequestCreationDto creationRequestDto) {
        MentorshipRequestDto responseDto = mentorshipRequestService.requestMentorship(creationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Get mentorship requests based on filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully")
    })
    @PostMapping("/filter")
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestBody RequestFilterDto filterDto) {
        List<MentorshipRequestDto> responseDtos = mentorshipRequestService.getRequests(filterDto);
        return ResponseEntity.ok(responseDtos);
    }

    @Operation(summary = "Accept a mentorship request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentorship request accepted successfully"),
            @ApiResponse(responseCode = "404", description = "Mentorship request not found")
    })
    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<MentorshipRequestDto> acceptRequest(@PathVariable Long requestId) {
        MentorshipRequestDto responseDto = mentorshipRequestService.acceptRequest(requestId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Reject a mentorship request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentorship request rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Mentorship request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@PathVariable Long requestId, @Valid @RequestBody RejectionDto rejectionDto) {
        MentorshipRequestDto responseDto = mentorshipRequestService.rejectRequest(requestId, rejectionDto);
        return ResponseEntity.ok(responseDto);
    }
}