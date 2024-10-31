package school.faang.user_service.exception.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillDtoFieldConstraintValidationException extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(SkillDtoFieldConstraintValidationException.class);

    public SkillDtoFieldConstraintValidationException(String message) {
        super(message);
        logger.error(message);
    }
}
