package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Validated
@RequestMapping("api/v1/mentorship")
@RestController
@RequiredArgsConstructor
@Tag(name = "Mentorship Controller", description = "Controller for managing mentorship")
@ApiResponse(responseCode = "400", description = "Invalid mentor ID")
@ApiResponse(responseCode = "500", description = "Server error")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Operation(
            summary = "Get mentees of a mentor",
            description = "Retrieve all mentees of a mentor by mentor ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of mentees successfully retrieved")
            }
    )
    @GetMapping("/mentors/{mentorId}/mentees")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getMentees(
            @PathVariable @NotNull(message = "Mentor ID should not be null") Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @Operation(
            summary = "Get mentors of a mentee",
            description = "Retrieve all mentors of a mentee by mentee ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of mentors successfully retrieved")
            }
    )
    @GetMapping("/mentee/{menteeId}/mentors")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getMentors(
            @PathVariable @NotNull(message = "Mentee ID should not be null") Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @Operation(
            summary = "Remove a mentee from a mentor",
            description = "Remove a mentee from a mentor using mentee ID and mentor ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mentee successfully removed from mentor")
            }
    )
    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(
            @PathVariable("menteeId") @NotNull(message = "Mentee ID should not be null") Long menteeId,
            @PathVariable("mentorId") @NotNull(message = "Mentor ID should not be null") Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Operation(
            summary = "Remove a mentor from a mentee",
            description = "Remove a mentor from a mentee using mentee ID and mentor ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mentor successfully removed from mentee")
            }
    )
    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(
            @PathVariable("menteeId") @NotNull(message = "Mentee ID should not be null") Long menteeId,
            @PathVariable("mentorId") @NotNull(message = "Mentor ID should not be null") Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
