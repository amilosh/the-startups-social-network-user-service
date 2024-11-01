package school.faang.user_service.conreoller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.service.goal.GoalInvitationService;

@RestController
@RequestMapping("/goal-invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationMapper goalInvitationMapper;

    @PostMapping
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        GoalInvitation newGoalInvitation = goalInvitationMapper.toEntity(invitation);
        GoalInvitation goalInvitation = goalInvitationService.createInvitation(newGoalInvitation, invitation.getInviterId(),
                invitation.getInvitedUserId(), invitation.getGoalId());
        return goalInvitationMapper.toDto(goalInvitation);
    }

    public void acceptGoalInvitation(long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }
}
