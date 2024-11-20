package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/{userId}")
    public GoalDto createGoal(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId,
            @Valid @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(
            @PathVariable @NotNull(message = "Goal ID should not be null") Long goalId,
            @Valid @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable @NotNull(message = "Goal ID should not be null") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(
            @PathVariable @NotNull(message = "Goal ID should not be null") Long goalId,
            @Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @GetMapping("/users/{userId}")
    public List<GoalDto> getGoalsByUser(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId,
            @Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }
}
