package school.faang.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.entity.goal.GoalStatus;

public class GoalStatusValidator implements ConstraintValidator<EnumeratedValid, GoalStatus> {

    @Override
    public void initialize(EnumeratedValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(GoalStatus status, ConstraintValidatorContext context) {
        if (status == null) {
            return true;
        }
        return status == GoalStatus.ACTIVE || status == GoalStatus.COMPLETED;
    }
}
