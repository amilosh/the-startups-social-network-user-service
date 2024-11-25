package school.faang.user_service.exception;

public class MinioException extends RuntimeException {
    public MinioException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioException(String message) {
        super(message);
    }
}
