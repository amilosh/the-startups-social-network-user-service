package school.faang.user_service.exception;

import java.util.List;

public class InvalidException extends RuntimeException {
    public InvalidException(List<String> message) {
        super((Throwable) message);
    }
}
