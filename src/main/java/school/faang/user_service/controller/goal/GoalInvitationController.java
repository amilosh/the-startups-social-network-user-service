package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal-invitations")
@Tag(name = "Goal Invitation")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    @Operation(summary = "Send an invite to join a goal")
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accept an invitation to a goal")
    public GoalInvitationDto acceptGoalInvitation(
            @PathVariable @NotNull(message = "Invitation ID should not be null") Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Decline an invitation to a goal")
    public GoalInvitationDto rejectGoalInvitation(
            @PathVariable @NotNull(message = "Invitation ID should not be null") Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping()
    @Operation(summary = "View all invitations with filters")
    public List<GoalInvitationDto> getInvitations(@Valid @RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitationsByFilter(filter);
    }
}
