package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/goals")
@Tag(name = "Goal Controller", description = "Controller for managing goals")
@ApiResponse(responseCode = "200", description = "Goal successfully updated")
@ApiResponse(responseCode = "201", description = "Goal successfully created")
@ApiResponse(responseCode = "204", description = "Goal successfully deleted")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Goal not found")
@ApiResponse(responseCode = "500", description = "Server error")
public class GoalController {
    private final GoalService goalService;
    private final UserContext userContext;

    @Operation(
            summary = "Create a new goal",
            description = "Create a new goal for the authenticated user"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponseDto createGoal(@Valid @RequestBody GoalRequestDto goalDto) {
        long userId = userContext.getUserId();
        return goalService.createGoal(userId,goalDto);
    }

    @Operation(
            summary = "Update an existing goal",
            description = "Update the details of an existing goal by its ID"
    )
    @PutMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalResponseDto updateGoal(
            @PathVariable @NotNull(message = "Goal ID should not be null") Long goalId,
            @Valid @RequestBody GoalRequestDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @Operation(
            summary = "Delete a goal",
            description = "Delete an existing goal by its ID"
    )
    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable @NotNull(message = "Goal ID should not be null") Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @Operation(
            summary = "Get subtasks for a goal",
            description = "Retrieve subtasks of a goal using its ID and optional filters"
    )
    @GetMapping("/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalResponseDto> findSubtasksByGoalId(
            @PathVariable @NotNull(message = "Goal ID should not be null") Long goalId,
            @Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.findSubtasksByGoalId(goalId, filters);
    }

    @Operation(
            summary = "Get goals for a user",
            description = "Retrieve all goals associated with a user by their ID"
    )
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalResponseDto> getGoalsByUser(
            @PathVariable @NotNull(message = "User ID should not be null") Long userId,
            @Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.getGoalsByUser(userId, filters);
    }
}