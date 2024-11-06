package school.faang.user_service.validation.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private static final int MAX_GOALS_PER_USER = 3;
    private static final int MAX_LENGTH_TITLE = 64;
    private static final int MAX_LENGTH_DESCRIPTION = 128;

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    private final SkillService skillService;

    /**
     * Validates a goal request and throws a DataValidationException if the request is invalid.
     *
     * @param userId   the ID of the user who is creating the goal
     * @param goal     the goal to create or update
     * @param isCreate whether this is a create or update request
     * @throws DataValidationException if the request is invalid
     */
    public void validateGoalRequest(Long userId, GoalDto goal, boolean isCreate) {
        if (userId == 0) {
            throw new DataValidationException("User ID is missing");
        }

        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("User does not exist");
        }

        if (goal == null) {
            throw new DataValidationException("Goal is missing");
        }

        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new DataValidationException("Goal title is missing");
        }

        if (goal.getTitle().length() > MAX_LENGTH_TITLE) {
            throw new DataValidationException("Goal title is too long");
        }

        if (goal.getDescription() == null) {
            throw new DataValidationException("Goal description is missing");
        }

        if (goal.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new DataValidationException("Goal description is too long");
        }

        if (goal.getStatus() == null) {
            throw new DataValidationException("Goal status is missing");
        }

        if (goal.getId() == null && !isCreate) {
            throw new DataValidationException("Goal ID does not exist");
        }
        if (!isCreate && !goalRepository.existsById(goal.getId())) {
            throw new DataValidationException("Goal does not exist");
        }

        if (isCreate && goalRepository.countActiveGoalsPerUser(userId) >= MAX_GOALS_PER_USER) {
            throw new DataValidationException("User already has the maximum number of active goals");
        }

        if (goal.getSkillIds() == null) {
            throw new DataValidationException("Skill IDs are missing");
        }

        for (Long skillId : goal.getSkillIds()) {
            if (!skillService.checkIfSkillExistsById(skillId)) {
                throw new DataValidationException("One of the skill IDs does not exist");
            }
        }
    }
}
