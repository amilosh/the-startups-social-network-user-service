package school.faang.user_service.exception;

public class StorageException extends RuntimeException {
    public StorageException(String message, Throwable reason) {
        super(message, reason);
    }
}
