package school.faang.user_service.exception;

import school.faang.user_service.exception.ValidateException;

public class SkillResourceNotFoundException extends ValidateException {
    public SkillResourceNotFoundException(String message) {
        super(message);
    }
}
