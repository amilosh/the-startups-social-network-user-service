package school.faang.user_service.validator.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.goal.GoalInvitationNullObjectException;

@Component
public class GoalInvitationValidator {

    public void validateUserExists(User user) {
        if (user == null) {
            throw new GoalInvitationNullObjectException("Пользователь не найден в БД");
        }
    }

    public void validateGoalExists(Goal goal) {
        if (goal == null) {
            throw new GoalInvitationNullObjectException("Цель не найден в БД");
        }
    }
}
