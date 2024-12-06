package school.faang.user_service.exceptions;

public class PremiumSubscriptionException extends RuntimeException{
    public PremiumSubscriptionException(String message) {
        super(message);
    }
    public PremiumSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
