package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> createGoal(@PathVariable Long userId,
                                           @Valid @RequestBody GoalDto goalDto) {
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{goalId}")
                        .buildAndExpand(goalService.createGoal(userId, goalDto).getId())
                        .toUri()
        ).build();
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Void> updateGoal(@PathVariable("goalId") Long goalId,
                                           @Valid @RequestBody GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("goalId") Long goalId) {
        goalService.deleteGoal(goalId);

        return ResponseEntity.noContent().build();
    }
}
