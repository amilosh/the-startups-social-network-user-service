package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/create")
    public GoalDto createGoal(@RequestParam Long userId, @RequestBody GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PutMapping("/update/{goalId}")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/delete/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @GetMapping("/user/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}

