package school.faang.user_service.conreoller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@RestController
@RequestMapping("/goal-invitations")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @Autowired
    public GoalInvitationController(GoalInvitationService goalInvitationService) {
        this.goalInvitationService = goalInvitationService;
    }

    @PostMapping
    public ResponseEntity<Void> createInvitation(GoalInvitationDto goalInvitationDto) {
        goalInvitationService.createInvitation(goalInvitationDto);
        return ResponseEntity.ok().build();
    }
}
