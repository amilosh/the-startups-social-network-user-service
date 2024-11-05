package school.faang.user_service.exception.skill;

import school.faang.user_service.exception.ValidateException;

public class SkillDtoFieldConstraintValidationException extends ValidateException {
    public SkillDtoFieldConstraintValidationException(String message) {
        super(message);
    }
}