package school.faang.user_service.exception;

public class SkillDuplicateException extends RuntimeException{
    public SkillDuplicateException(String message) {
        super(message);
    }
}
