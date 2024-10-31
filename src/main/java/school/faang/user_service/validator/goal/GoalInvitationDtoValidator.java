package school.faang.user_service.validator.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.goal.GoalInvitationEqualsException;
import school.faang.user_service.exception.goal.GoalInvitationNullObjectException;

@Component
public class GoalInvitationDtoValidator {

    public void validate(GoalInvitationDto invitation) {
        if (invitation.getInviterId() == null) {
            throw new GoalInvitationNullObjectException("Inviter ID can't be empty!");
        }

        if (invitation.getInvitedUserId() == null) {
            throw new GoalInvitationNullObjectException("Invited User ID can't be empty!");
        }

        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new GoalInvitationEqualsException("Invitee and his ID can't be the same");
        }

        if (invitation.getStatus() == null) {
            throw new GoalInvitationNullObjectException("Invalid status");
        }

        if (invitation.getGoalId() == null) {
            throw new GoalInvitationNullObjectException("Goal ID can't be empty!");
        }
    }
}
