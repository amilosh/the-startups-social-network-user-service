package school.faang.user_service.controller.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        return goalInvitationService.createInvitation(goalInvitationDto);
    }

    @PatchMapping("/accept/{id}")
    public GoalInvitationDto acceptInvitation(@PathVariable @Positive long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("/reject/{id}")
    public GoalInvitationDto rejectInvitation(@PathVariable @Positive long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/search")
    public List<GoalInvitationDto> getAllInvitations(@RequestBody InvitationFilterDto invitationFilterDto) {
        return goalInvitationService.getInvitations(invitationFilterDto);
    }
}
