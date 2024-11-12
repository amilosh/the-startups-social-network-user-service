package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Validated
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto) {
        return goalInvitationService.createInvitation(goalInvitationDto);
    }

    public GoalInvitationDto acceptInvitation(@NotNull @Positive long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    public GoalInvitationDto rejectInvitation(@NotNull @Positive long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getAllInvitations(InvitationFilterDto invitationFilterDto) {
        return goalInvitationService.getInvitations(invitationFilterDto);
    }


}
