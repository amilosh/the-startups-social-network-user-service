package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Validated
@RequestMapping("api/v1/mentorship")
@RestController
@RequiredArgsConstructor
@Tag(name = "Mentorship controller", description = "Controller for managing mentorship")
@ApiResponse(description = "Successful execution", responseCode = "200")
@ApiResponse(description = "Client error", responseCode = "400")
@ApiResponse(description = "Server error", responseCode = "500")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Operation(
            summary = "Get mentees of a mentor",
            description = "Retrieve all mentees of a mentor by mentor ID"
    )
    @GetMapping("/mentors/{mentorId}/mentees")
    public List<UserDto> getMentees(
            @PathVariable @NotNull(message = "Mentor ID should not be null") Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @Operation(
            summary = "Get mentors of a mentee",
            description = "Retrieve all mentors of a mentee by mentee ID"
    )
    @GetMapping("/mentee/{menteeId}/mentors")
    public List<UserDto> getMentors(
            @PathVariable @NotNull(message = "Mentee ID should not be null") Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @Operation(
            summary = "Remove a mentee from a mentor",
            description = "Remove a mentee from a mentor using mentee ID and mentor ID"
    )
    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(
            @PathVariable("menteeId") @NotNull(message = "Mentee ID should not be null") Long menteeId,
            @PathVariable("mentorId") @NotNull(message = "Mentor ID should not be null") Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @Operation(
            summary = "Remove a mentor from a mentee",
            description = "Remove a mentor from a mentee using mentee ID and mentor ID"
    )
    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(
            @PathVariable("menteeId") @NotNull(message = "Mentee ID should not be null") Long menteeId,
            @PathVariable("mentorId") @NotNull(message = "Mentor ID should not be null") Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
