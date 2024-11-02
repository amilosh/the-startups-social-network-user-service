package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDTO;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.goal.responce.ValidationResponse;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalValidation {
    private static final int MAX_GOALS_PER_USER = 3;
    private static final int MAX_LENGTH_TITLE = 64;
    private static final int MAX_LENGTH_DESCRIPTION = 128;

    private final ValidationResponse response = new ValidationResponse();

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    private final SkillService skillService;

    /**
     * Validates a create goal request.
     *
     * @param userId the user ID, must not be 0
     * @param goal   the goal to validate, must not be null
     * @return a {@link ValidationResponse} which contains the result of the validation
     * <p>
     * The validation will fail if the user ID is 0, the goal is missing, the goal title is missing, the goal title is empty, the goal title is too long, the goal description is too long, the goal status is missing, the user already has the maximum number of active goals, the skill IDs are missing, or one of the skill IDs does not exist.
     */
    public ValidationResponse validateGoalRequest(Long userId, GoalDTO goal, boolean isCreate) {
        List<String> errors = new ArrayList<>();

        if (userId == 0) {
            errors.add("User ID is missing");
        }

        if (!userRepository.existsById(userId)) {
            errors.add("User does not exist");
        }

        if (goal == null) {
            errors.add("Goal is missing");
            response.setErrors(errors);
            return response;
        }

        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            errors.add("Goal title is missing");
            response.setErrors(errors);
            return response;
        }

        if (goal.getTitle().length() > MAX_LENGTH_TITLE) {
            errors.add("Goal title is too long");
        }

        if (goal.getDescription() == null || goal.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            errors.add("Goal description is missing");
        }

        if (goal.getStatus() == null) {
            errors.add("Goal status is missing");
        }

        if (isCreate && goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER) {
            errors.add("User cannot have more than %s active goals".formatted(MAX_GOALS_PER_USER));
        }

        if (goal.getSkillIds() == null) {
            errors.add("Skill IDs is missing");
            response.setErrors(errors);
            return response;
        }

        for (Long skillId : goal.getSkillIds()) {
            if (!skillService.checkIfSkillExistsById(skillId)) {
                errors.add("Skill with ID: %s does not exist".formatted(skillId));
            }
        }

        if (!errors.isEmpty()) {
            response.setValid(false);
            response.setErrors(errors);
            return response;
        }

        response.setValid(true);
        return response;
    }
}
