package school.faang.user_service.validator.goal;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService.SkillService;

import java.util.List;

@Data
@Component
@RequiredArgsConstructor
public class GoalValidator {

    private static final int MAX_USER_GOALS_LIMIT = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void validateGoal(long userId, GoalDto goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) == MAX_USER_GOALS_LIMIT) {
            throw new DataValidationException("Reached maximum quantity of goals");
        }
        List<Skill> skills = skillRepository.findAllById(goal.getSkillIds());
        if (skills.size() != goal.getSkillIds().size()) {
            throw new DataValidationException("Some skills do not exist in the database.");
        }
    }
}