package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void loadUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Такого пользователя в БД не существует"));
    }
}
