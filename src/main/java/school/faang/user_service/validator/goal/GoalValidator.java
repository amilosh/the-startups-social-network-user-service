package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalValidator {

    private static final int MAX_USER_GOALS_LIMIT = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void validateCreationGoal(Long userId, GoalRequestDto goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_USER_GOALS_LIMIT) {
            log.warn("User {} reached the maximum quantity of active goals: {}", userId, MAX_USER_GOALS_LIMIT);
            throw new DataValidationException("Reached maximum quantity of goals");
        }
        validateSkillsExist(goal.getSkillIds());
    }

    public void validateUpdatingGoal(Long goalId, GoalRequestDto goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            log.warn("Attempt to update a completed goal: goalId {}", goalId);
            throw new IllegalArgumentException("Cannot update a completed goal");
        }
        validateSkillsExist(goal.getSkillIds());
    }

    private void validateSkillsExist(List<Long> skillIds) {
        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            log.error("Some skills do not exist in the database: expected {}, found {}",
                    skillIds.size(), skills.size());
            throw new DataValidationException("Some skills do not exist in the database.");
        }
    }
}