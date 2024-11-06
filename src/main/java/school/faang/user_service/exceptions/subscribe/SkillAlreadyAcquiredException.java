package school.faang.user_service.exceptions.subscribe;

public class SkillAlreadyAcquiredException extends RuntimeException {
    public SkillAlreadyAcquiredException(String message) {
        super(message);
    }
}
