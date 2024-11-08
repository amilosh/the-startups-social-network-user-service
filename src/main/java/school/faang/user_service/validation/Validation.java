package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class Validation {

    private final UserRepository userRepository;

    public void validateIdCorrect(long id) {
        if (id <= 0) {
            throw new DataValidationException("Incorrect id");
        }
    }

    public User validateUserData(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new DataValidationException("User is null"));
    }
}
