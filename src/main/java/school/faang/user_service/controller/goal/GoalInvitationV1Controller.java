package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goalInvitation")
@RequiredArgsConstructor
@Validated
public class GoalInvitationV1Controller {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public GoalInvitationDto createInvitation(@RequestBody @Validated GoalInvitationDto goalInvitationDto) {
        return goalInvitationService.createInvitation(goalInvitationDto);
    }

    @PutMapping("/{id}/accept")
    public GoalInvitationDto acceptInvitation(@PathVariable @Positive long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    public GoalInvitationDto rejectInvitation(@PathVariable @Positive long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/search")
    public List<GoalInvitationDto> getAllInvitations(@RequestBody @Validated InvitationFilterDto invitationFilterDto) {
        return goalInvitationService.getInvitations(invitationFilterDto);
    }
}
