package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id #" + userId + " not found");
        }
    }
}
