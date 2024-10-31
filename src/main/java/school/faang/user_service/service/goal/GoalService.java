package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidation;
import school.faang.user_service.validation.goal.responce.ValidationResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    private final UserService userService;
    private final SkillService skillService;

    private final GoalMapper goalMapper;

    private final GoalValidation goalValidation;


    /**
     * Creates a new goal and saves it to the database.
     *
     * @param userId the ID of the user who is creating the goal
     * @param goal the goal to create
     * @return a response containing the created goal
     */
    public GoalResponse createGoal(Long userId, GoalDTO goal) {
        ValidationResponse validationResponse = goalValidation.validateCreateGoalRequest(userId, goal);

        if (!validationResponse.isValid()) {
            var response = new GoalResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(validationResponse.getErrors());
            return response;
        }

        Goal entity = goalMapper.toEntity(goal);
        entity.setUsers(List.of(userService.getUserById(userId)));

        entity.setSkillsToAchieve(goal.getSkillIds().stream().map(skillService::getSkillById).toList());

        goalRepository.save(entity);

        GoalResponse response = new GoalResponse(
                "Goal created successfully",
                201
        );
        response.setData(goal);

        return response;
    }
}
