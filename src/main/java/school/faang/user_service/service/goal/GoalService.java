package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
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
     * @param goal   the goal to create
     * @return a response containing the created goal
     */
    public GoalResponse createGoal(Long userId, GoalDTO goal) {
        ValidationResponse validationResponse = goalValidation.validateGoalRequest(userId, goal);

        if (!validationResponse.isValid()) {
            var response = new GoalResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(validationResponse.getErrors());
            return response;
        }

        goal.setStatus(GoalStatus.ACTIVE);
        goal.setId(null);

        Goal entity = goalMapper.toEntity(goal);
        entity.setUsers(List.of(userService.getUserById(userId)));

        entity.setSkillsToAchieve(goal.getSkillIds().stream().map(skillService::getSkillById).toList());

        goalRepository.save(entity);

        GoalResponse response = new GoalResponse(
                "Goal created successfully",
                201
        );
        response.setData(goalMapper.toDto(entity));

        return response;
    }

    /**
     * Updates an existing goal with the provided details.
     *
     * @param userId the ID of the user updating the goal
     * @param goal the updated goal details
     * @return a response containing the result of the update operation
     *         with status 201 if successful or 400 if validation fails
     *
     * The update will fail if the validation of the goal request fails or if the goal does not exist.
     */
    public GoalResponse updateGoal(Long userId, GoalDTO goal) {
        ValidationResponse validationResponse = goalValidation.validateGoalRequest(userId, goal);

        if (!validationResponse.isValid()) {
            var response = new GoalResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(validationResponse.getErrors());
            return response;
        }

        if (!goalValidation.checkIfGoalExistsByID(goal.getId())) {
            var response = new GoalResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(List.of("Goal does not exist"));
            return response;
        }

        Goal entity = goalRepository.findGoalById(goal.getId());

        entity.setTitle(goal.getTitle());
        entity.setDescription(goal.getDescription());
        entity.setStatus(goal.getStatus());
        entity.setDeadline(goal.getDeadline());
        entity.setUsers(List.of(userService.getUserById(userId)));
        entity.setSkillsToAchieve(goal.getSkillIds().stream().map(skillService::getSkillById).toList());

        if (goal.getMentorId() != null) {
            entity.setMentor(userService.getUserById(goal.getMentorId()));
        }

        if (goal.getParentGoalId() != null) {
            entity.setParent(goalRepository.findGoalById(goal.getParentGoalId()));
        }

        goalRepository.updateGoal(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus().name().equals("ACTIVE") ? 0 : 1,
                entity.getDeadline(),
                entity.getParent(),
                entity.getMentor() == null ? null : entity.getMentor().getId()
        );

        GoalResponse response = new GoalResponse(
                "Goal updated successfully",
                201
        );
        response.setData(goalMapper.toDto(entity));

        return response;
    }


    /**
     * Deletes a goal by its ID.
     *
     * @param goalId the ID of the goal to delete
     * @return a response containing the result of the delete operation
     *         with status 204 if successful or 400 if validation fails
     *
     * The deletion will fail if the goal does not exist.
     */
    public GoalResponse deleteGoal(long goalId) {
        if (!goalValidation.checkIfGoalExistsByID(goalId)) {
            var response = new GoalResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(List.of("Goal does not exist"));
            return response;
        }

        goalRepository.delete(goalRepository.findGoalById(goalId));

        return new GoalResponse(
                "Goal deleted successfully",
                204
        );
    }
}
