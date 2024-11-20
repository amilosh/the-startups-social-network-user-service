package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class UserValidator {

    public void validateUserExistence(boolean isExist) {
        if (!isExist) {
            throw new EntityNotFoundException("User not exists");
        }
    }
}
