package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.UserNotExistsException;
import school.faang.user_service.repository.UserRepository;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {
    private final UserRepository repository;

    public boolean isUserExists(Long id) {
        if (!repository.existsById(id)) {
            log.warn("User with id '{}' not exists.", id);
            throw new UserNotExistsException("User with id '" + id + "' not exists.");
        }
        return true;
    }
}
