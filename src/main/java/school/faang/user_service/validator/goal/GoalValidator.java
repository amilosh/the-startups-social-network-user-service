package school.faang.user_service.validator.goal;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService.SkillService;

@Data
@Component
@RequiredArgsConstructor
public class GoalValidator {

    private static final int MAX_USER_GOALS_LIMIT = 3;

    private final GoalRepository goalRepository;
    private final SkillService skillService;

         public void validateGoal(long userId, GoalDto goal) {
            if (goalRepository.countActiveGoalsPerUser(userId) == MAX_USER_GOALS_LIMIT){
                throw new DataValidationException("Reached maximum quantity of goals");
            }
            if (goal.getSkillIds() != null && !goal.getSkillIds().stream().allMatch(skillService::existsById)) {
                throw new DataValidationException("Incorrect skills ");
            }
        }

    }