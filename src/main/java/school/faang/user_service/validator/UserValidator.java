package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

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

    public void areUsersIdEqual(long firstUserId, long secondUserId) {
        if (firstUserId == secondUserId) {
            throw new DataValidationException("Id of inviter and invited user can't be the same");
        }
    }
}
