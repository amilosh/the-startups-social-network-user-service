package school.faang.user_service.exception;

public class InvalidFileFormatException extends RuntimeException {
    public InvalidFileFormatException(String message) {
        super(message);
    }

    public InvalidFileFormatException(String message, Throwable reason) {
        super(message, reason);
    }
}
