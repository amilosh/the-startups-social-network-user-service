package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void isUserExists(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id: " + userId + " not found in DB"));
    }

    public void areUsersExist(long firstUserId, long secondUserId) {
        isUserExists(firstUserId);
        isUserExists(secondUserId);
    }

    public Stream<User> validateNotPremiumUsers() {
        Stream<User> users = userRepository.findAll().stream();

        return users.filter(user -> user.getPremium() == null
                || user.getPremium().getEndDate() == null
                || user.getPremium().getEndDate().isBefore(LocalDateTime.now()));
    }
}
