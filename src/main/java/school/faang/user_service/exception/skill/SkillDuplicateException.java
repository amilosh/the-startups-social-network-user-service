package school.faang.user_service.exception.skill;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SkillDuplicateException extends RuntimeException{

    public SkillDuplicateException(String message) {
        super(message);
    }
}
