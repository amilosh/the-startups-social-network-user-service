package school.faang.user_service.exceptions;

public class DuplicateObjectException extends RuntimeException {

    public DuplicateObjectException(String message) {
        super(message);
    }
}