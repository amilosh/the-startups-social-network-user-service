package school.faang.user_service.controller.goal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.entity.goal.Goal;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestParam Long userId, @RequestBody Goal goal, @RequestParam List<Long> skillIds) {
        Optional<Goal> createdGoal = goalService.createGoal(userId, goal, skillIds);

        return createdGoal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        Optional<Goal> updatedGoal = goalService.updateGoal(goalId, goalDto);

        return updatedGoal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        try {
            goalService.deleteGoal(goalId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
