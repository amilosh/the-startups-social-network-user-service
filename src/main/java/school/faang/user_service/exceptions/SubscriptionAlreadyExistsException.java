package school.faang.user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SubscriptionAlreadyExistsException extends RuntimeException{
    public SubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
