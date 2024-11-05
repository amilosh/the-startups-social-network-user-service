package school.faang.user_service.exceptions.subscribe;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParticipantRegistrationException extends RuntimeException {
    public ParticipantRegistrationException(String message) {
        super(message);
    }
}
