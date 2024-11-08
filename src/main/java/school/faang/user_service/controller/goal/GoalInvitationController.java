package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        log.info("Received request to create invitation: {}", invitation);
        return goalInvitationService.createInvitation(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        log.info("Received request to accept invitation with id: {}", id);
        return goalInvitationService.acceptGoalInvitation(id);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        log.info("Received request to reject invitation with id: {}", id);
        return goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        log.info("Received request to get invitations with filter: {}", filter);
        return goalInvitationService.getInvitations(filter);
    }
}
