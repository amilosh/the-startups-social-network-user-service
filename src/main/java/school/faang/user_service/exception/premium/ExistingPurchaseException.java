package school.faang.user_service.exception.premium;

public class ExistingPurchaseException extends RuntimeException {
    public ExistingPurchaseException(String message, Object... args) {
        super(String.format(message, args));
    }
}
