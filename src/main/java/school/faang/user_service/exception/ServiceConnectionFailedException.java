package school.faang.user_service.exception;

public class ServiceConnectionFailedException extends RuntimeException {
    public ServiceConnectionFailedException(String message) {
        super(message);
    }
}
