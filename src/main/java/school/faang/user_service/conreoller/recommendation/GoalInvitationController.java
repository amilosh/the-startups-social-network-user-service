package school.faang.user_service.conreoller.recommendation;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@RestController
@RequestMapping("/recommendation-invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public void getInvitations(InvitationFilterIDto filter){

    }
}
