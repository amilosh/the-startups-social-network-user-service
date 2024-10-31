package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.goal.GoalInvitationDtoValidator;

@Component
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;
    private final GoalInvitationDtoValidator goalInvitationDtoValidator;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        goalInvitationDtoValidator.validate(invitation);
        return goalInvitationService.createInvitation(invitation);
    }
}
