package school.faang.user_service.exception;

public class PremiumPeriodNotFoundException extends RuntimeException {
    public PremiumPeriodNotFoundException(String message) {
        super(message);
    }
}
