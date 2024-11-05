package school.faang.user_service.exception.user;

import school.faang.user_service.exception.ValidateException;

public class UserResourceNotFoundException extends ValidateException {
    public UserResourceNotFoundException(String message) {
        super(message);
    }
}
