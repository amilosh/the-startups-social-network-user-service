package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationValidator;

@Component
@RequiredArgsConstructor
@Validated
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationValidator goalInvitationValidator;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateDto(invitation);
        return goalInvitationService.createInvitation(invitation);
    }

    public void acceptGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        goalInvitationService.rejectGoalInvitation(id);
    }

}
