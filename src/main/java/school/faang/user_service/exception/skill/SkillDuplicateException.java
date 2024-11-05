package school.faang.user_service.exception.skill;

import school.faang.user_service.exception.ValidateException;

public class SkillDuplicateException extends ValidateException {
    public SkillDuplicateException(String message) {
        super(message);
    }
}
