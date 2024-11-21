package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UserServiceValidator {
    public void validateUser(long contextUserId, long userId) {
        if (contextUserId != userId) {
            throw new DataValidationException(String.format("A user can get data only about his own followers. " +
                    "Context user id = %d, userId = %d", contextUserId, userId));
        }
    }
}
