package school.faang.user_service.exception;

public class RequestFrequencyException extends RuntimeException {
    public RequestFrequencyException(String message) {
        super(message);
    }
}
