package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationValidator goalInvitationValidator;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateDto(invitation);
        return goalInvitationService.createInvitation(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationService.acceptGoalInvitation(id);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
