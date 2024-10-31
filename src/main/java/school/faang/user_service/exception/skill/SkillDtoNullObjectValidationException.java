package school.faang.user_service.exception.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillDtoNullObjectValidationException extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(SkillDtoNullObjectValidationException.class);

    public SkillDtoNullObjectValidationException(String message) {
        super(message);
        logger.error(message);
    }
}
