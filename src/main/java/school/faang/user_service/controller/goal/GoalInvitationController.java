package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationRequestDto;
import school.faang.user_service.dto.goal.GoalInvitationResponseDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal-invitations")
@Tag(name = "Goal Invitation Controller", description = "Controller for managing goal invitations")
@ApiResponse(responseCode = "201", description = "Invitation successfully created")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Invitation not found")
@ApiResponse(responseCode = "500", description = "Server error")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @Operation(
            summary = "Send an invitation to join a goal",
            description = "Allows a user to invite another user to collaborate on a goal"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationResponseDto createInvitation(@Valid @RequestBody GoalInvitationRequestDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @Operation(
            summary = "Accept an invitation to a goal",
            description = "Accept an invitation by its ID to collaborate on a goal",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation successfully accepted")
            }
    )
    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public GoalInvitationResponseDto acceptGoalInvitation(
            @PathVariable @NotNull(message = "Invitation ID should not be null") Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @Operation(
            summary = "Reject an invitation to a goal",
            description = "Decline an invitation by its ID to collaborate on a goal",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation successfully rejected")
            }
    )
    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public GoalInvitationResponseDto rejectGoalInvitation(
            @PathVariable @NotNull(message = "Invitation ID should not be null") Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @Operation(
            summary = "View all invitations with filters",
            description = "Retrieve a list of invitations filtered by criteria",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitations successfully retrieved")
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GoalInvitationResponseDto> getInvitations(@Valid @ModelAttribute InvitationFilterDto filter) {
        return goalInvitationService.getInvitationsByFilter(filter);
    }
}
