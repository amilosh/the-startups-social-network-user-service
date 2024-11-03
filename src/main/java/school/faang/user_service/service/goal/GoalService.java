package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.dto.response.GoalResponse;
import school.faang.user_service.dto.response.GoalsResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidation;
import school.faang.user_service.validation.goal.responce.ValidationResponse;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    private final UserService userService;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;

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
        ValidationResponse validationResponse = goalValidation.validateGoalRequest(userId, goal, true);

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

        if (goal.getParentGoalId() != null) {
            entity.setParent(goalRepository.findGoalById(goal.getParentGoalId()));
        }

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
     * @param goal   the updated goal details
     * @return a response containing the result of the update operation
     * with status 201 if successful or 400 if validation fails
     * <p>
     * The update will fail if the validation of the goal request fails or if the goal does not exist.
     */
    public GoalResponse updateGoal(Long userId, GoalDTO goal) {
        ValidationResponse validationResponse = goalValidation.validateGoalRequest(userId, goal, false);

        if (!validationResponse.isValid()) {
            var response = new GoalResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(validationResponse.getErrors());
            return response;
        }

        if (!goalRepository.existsById(goal.getId())) {
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
     * with status 204 if successful or 400 if validation fails
     * <p>
     * The deletion will fail if the goal does not exist.
     */
    public GoalResponse deleteGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
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

    /**
     * Gets a list of goals for a given user ID, with optional filters.
     *
     * @param userId  the ID of the user to get goals for
     * @param filters a DTO containing optional filters to apply
     * @return a response containing the list of filtered goals
     * with status 200 if successful or 400 if validation fails
     * <p>
     * If the user does not exist, the method will return a response with a 400
     * status and an error message.
     */
    public GoalsResponse getGoalsByUser(long userId, GoalFilterDto filters) {
        if (!userService.checkIfUserExistsById(userId)) {
            var response = new GoalsResponse(
                    "Validation failed",
                    400
            );
            response.setErrors(List.of("Goal does not exist"));
            return response;
        }

        Stream<Goal> goals = goalRepository.findAll().stream();

        Stream<Goal> filteredGoals = goalFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        goals,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        var response = new GoalsResponse(
                "Goal fetched successfully",
                200
        );
        response.setData(filteredGoals.map(goalMapper::toDto).toList());

        return response;
    }
}
