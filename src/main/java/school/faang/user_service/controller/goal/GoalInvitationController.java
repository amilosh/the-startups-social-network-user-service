package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goal-invitations")
@RequiredArgsConstructor
@Tag(name = "Goal Invitation")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping()
    @Operation(summary = "Send an invite to join a goal")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("accept/{id}")
    @Operation(summary = "Accept an invitation to a goal")
    public void acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("reject/{id}")
    @Operation(summary = "Decline an invitation to a goal")
    public void rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @PutMapping("/filters")
    @Operation(summary = "View all invitations with filters")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterIDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
