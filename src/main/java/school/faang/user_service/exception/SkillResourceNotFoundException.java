package school.faang.user_service.exception.skill;

import school.faang.user_service.exception.ValidateException;

public class SkillResourceNotFoundException extends ValidateException {
    public SkillResourceNotFoundException(String message) {
        super(message);
    }
}
