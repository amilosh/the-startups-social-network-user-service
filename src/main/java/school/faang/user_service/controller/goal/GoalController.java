package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.request.CreateGoalRequest;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    /**
     * Endpoint to create a new goal for a given user.
     *
     * @param request the request payload containing user ID and goal details
     * @return the newly created goal
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalDto> createGoal(@RequestBody CreateGoalRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(goalService.createGoal(request.getUserId(), request.getGoal()));
    }

    /**
     * Endpoint to update an existing goal. The request body should contain the user ID of the user updating the goal
     * and the updated goal details.
     *
     * @param request the request payload containing user ID and updated goal details
     * @return a response containing the updated goal
     *
     * The update will fail if the goal does not exist.
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalDto> updateGoal(@RequestBody CreateGoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(request.getUserId(), request.getGoal()));
    }

    /**
     * Endpoint to delete a goal by its ID.
     *
     * @param goalId the ID of the goal to be deleted
     *
     * The deletion will fail if the goal does not exist.
     */
    @DeleteMapping(value = "/{goalId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Endpoint to retrieve a list of goals for a given user ID, filtered by the provided goal filter.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @param filters the goal filter to apply on the retrieved goals
     * @return a list of goals for the given user ID, filtered by the provided goal filter
     */
    @PostMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GoalDto>> getGoalsByUser(@PathVariable long userId, @RequestBody GoalFilterDto filters) {
        return ResponseEntity.ok(goalService.getGoalsByUser(userId, filters));
    }
}
