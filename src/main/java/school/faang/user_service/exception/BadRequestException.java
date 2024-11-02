package school.faang.user_service.exception;

import java.util.List;

public class BadRequestException extends RuntimeException {
    public BadRequestException(List<String> messages) {
        super((Throwable) messages);
    }
}
