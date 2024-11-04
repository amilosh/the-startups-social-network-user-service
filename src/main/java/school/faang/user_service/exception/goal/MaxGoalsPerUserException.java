package school.faang.user_service.exception.goal;

public class MaxGoalsPerUserException extends RuntimeException {
    public MaxGoalsPerUserException(String message) {
        super(message);
    }
}
