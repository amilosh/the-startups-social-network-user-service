package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.request.GetGoalsByFilterRequest;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.dto.request.CreateGoalRequest;
import school.faang.user_service.dto.response.GoalsResponse;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.skill.SkillService;

@RestController()
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final SkillService skillService;

    /**
     * Endpoint to create a new goal.
     *
     * @param request the request payload containing user ID and goal details
     * @return a ResponseEntity containing the result of the goal creation process
     *         with status 201 if successful or 400 if validation fails
     */
    @PostMapping(consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createGoal(@RequestBody CreateGoalRequest request) {
        GoalResponse response = goalService.createGoal(request.getUserId(), request.getGoal());

        if (response.getCode() == 400) {
            throw new BadRequestException(response.getErrors());
        }

        return ResponseEntity.status(201).body(response);
    }

    /**
     * Endpoint to update an existing goal.
     *
     * @param request the request payload containing user ID and goal details
     * @return a ResponseEntity containing the result of the goal update process
     *         with status 201 if successful or 400 if validation fails
     */
    @PutMapping(consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalResponse> updateGoal(@RequestBody CreateGoalRequest request) {
        GoalResponse response = goalService.updateGoal(request.getUserId(), request.getGoal());

        if (response.getCode() == 400) {
            throw new BadRequestException(response.getErrors());
        }

        return ResponseEntity.status(201).body(response);
    }

    /**
     * Endpoint to delete an existing goal.
     *
     * @param goalId the ID of the goal to be deleted
     * @return a ResponseEntity containing the result of the goal deletion process
     *         with status 204 if successful
     */
    @DeleteMapping(value = "/{goalId}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalResponse> deleteGoal(@PathVariable Long goalId) {
        GoalResponse response = goalService.deleteGoal(goalId);

        if (response.getCode() == 400) {
            throw new BadRequestException(response.getErrors());
        }

        return ResponseEntity.status(204).body(response);
    }

    /**
     * Endpoint to get all goals for a given user, filtered by a set of filters.
     *
     * @param userId the ID of the user to get goals for
     * @param filters the filters to apply
     * @return a ResponseEntity containing the goals matching the given filters
     *         with status 200 if successful or 400 if filters are invalid
     */
    @PostMapping(value = "/{userId}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalsResponse> getGoalsByUser(@PathVariable long userId, @RequestBody GetGoalsByFilterRequest filters) {
        GoalsResponse response = goalService.getGoalsByUser(userId, filters.getFilters());

        if (response.getCode() == 400) {
            throw new BadRequestException(response.getErrors());
        }

        return ResponseEntity.ok(response);
    }
}
