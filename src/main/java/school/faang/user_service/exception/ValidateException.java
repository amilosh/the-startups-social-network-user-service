package school.faang.user_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import school.faang.user_service.exception.skill.SkillDtoFieldConstraintValidationException;

public class ValidateException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(SkillDtoFieldConstraintValidationException.class);

    public ValidateException(String message) {
        super(message);
        logger.error(message);
    }
}