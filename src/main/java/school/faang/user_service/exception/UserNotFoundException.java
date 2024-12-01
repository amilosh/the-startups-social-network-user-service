package school.faang.user_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User with Id " + userId + " not found.");
    }
}
