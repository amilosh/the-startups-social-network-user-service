package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/mentorship-request")
@Tag(name = "Mentorship Request", description = "Controller for managing mentorship requests")
@ApiResponse(description = "Successful method execution", responseCode = "200")
@ApiResponse(description = "Client error", responseCode = "400")
@ApiResponse(description = "Server error", responseCode = "500")
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @Operation(
            summary = "Request mentorship",
            description = "Send a mentorship request to another user"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @Operation(
            summary = "Get all mentorship requests",
            description = "Retrieve all mentorship requests using filters"
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipRequestDto> getRequests(@Valid @ModelAttribute RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @Operation(
            summary = "Accept mentorship request",
            description = "Allow a user to accept a mentorship request from another user"
    )
    @PutMapping("/accept/{mentorshipRequestId}")
    @ResponseStatus(HttpStatus.OK)
    public void acceptRequest(
            @PathVariable @NotNull(message = "Mentorship request ID should not be null") Long mentorshipRequestId) {
        mentorshipRequestService.acceptRequest(mentorshipRequestId);
    }

    @Operation(
            summary = "Reject mentorship request",
            description = "Allow a user to reject a mentorship request"
    )
    @PutMapping("/reject/{mentorshipRequestId}")
    @ResponseStatus(HttpStatus.OK)
    public void rejectRequest(
            @PathVariable @NotNull(message = "Mentorship request ID should not be null") Long mentorshipRequestId,
            @Valid @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(mentorshipRequestId, rejection);
    }
}
