package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public User validateUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id {} not found", userId);
            return new NoSuchElementException(String.format("There isn't user with id = %d", userId));
        });
    }

    public void validateUserIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new DataValidationException("List Ids shouldn't be empty");
        }
    }

    public void validateUsersWithIdExists(List<User> users) {
        if (users.isEmpty()) {
            throw new DataValidationException("There aren't found any users by these Ids/Id");
        }
    }
}