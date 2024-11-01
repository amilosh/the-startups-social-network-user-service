package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.dto.request.CreateGoalRequest;
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
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping(consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoalResponse> updateGoal(@RequestBody CreateGoalRequest request) {
        GoalResponse response = goalService.updateGoal(request.getUserId(), request.getGoal());

        if (response.getCode() == 400) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.status(201).body(response);
    }
}
