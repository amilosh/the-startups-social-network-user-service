package school.faang.user_service.exception.mentorship_request;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}
