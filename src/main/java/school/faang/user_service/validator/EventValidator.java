package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventValidator {

    public void validateRegistration(boolean isRegistered, boolean shouldBeRegistered) {
        if (shouldBeRegistered && isRegistered) {
            throw new DataValidationException("Пользователь уже зарегистрирован на событие");
        }

        if (!shouldBeRegistered && !isRegistered) {
            throw new DataValidationException("Пользователь не был зарегистрирован на событие");
        }
    }
}
