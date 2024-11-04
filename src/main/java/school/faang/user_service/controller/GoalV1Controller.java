package school.faang.user_service.controller;


import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.GoalService;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Validated
public class GoalV1Controller {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponseDto> createGoal(
            @Parameter(description = "New goal data")
            @RequestBody @Validated CreateGoalDto createGoalDto) {

        GoalResponseDto goalResponseDto = goalService.create(createGoalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(goalResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @Parameter(description = "Goal id")
            @PathVariable @Positive long id) {

        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GoalResponseDto> updateGoal(
            @Parameter(description = "GoalId")
            @PathVariable @Positive Long id,

            @Parameter(description = "Updated goal data")
            @RequestBody @Validated UpdateGoalDto updateGoalDto) {

        GoalResponseDto goalResponseDto = goalService.update(id, updateGoalDto);
        return ResponseEntity.ok(goalResponseDto);
    }
}
