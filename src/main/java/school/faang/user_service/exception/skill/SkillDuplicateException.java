package school.faang.user_service.exception.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillDuplicateException extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(SkillDuplicateException.class);

    public SkillDuplicateException(String message) {
        super(message);
        logger.error(message);
    }
}
