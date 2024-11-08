package school.faang.user_service.exceptions;

public class SkillAlreadyAcquiredException extends RuntimeException {
    public SkillAlreadyAcquiredException(String message) {
        super(message);
    }
}
