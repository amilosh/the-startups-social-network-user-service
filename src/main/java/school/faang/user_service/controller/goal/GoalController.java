package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
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
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@Valid @RequestBody GoalDto goalDto) {
        long userId = userContext.getUserId();
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(
            @PathVariable @NotNull(message = "Goal ID should not be null") Long goalId,
            @Valid @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable @NotNull(message = "Goal ID should not be null") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> findSubtasksByGoalId(
            @PathVariable @NotNull(message = "Goal ID should not be null") Long goalId,
            @Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> getGoalsByUser(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId,
            @Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }
}
