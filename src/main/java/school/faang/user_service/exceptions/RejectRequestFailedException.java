package school.faang.user_service.exceptions;

public class RejectRequestFailedException extends RuntimeException {
    public RejectRequestFailedException(String message) {
        super(message);
    }
}
