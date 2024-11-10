package school.faang.user_service.exceptions;

public class ValueExceededException extends RuntimeException {

    public ValueExceededException(String message) {
        super(message);
    }
}