package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Get a user by its ID.
     *
     * @param userId the ID of the user
     * @return the user, or null if the user does not exist
     */
    public User getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    /**
     * Checks if a user with the given ID exists in the database.
     *
     * @param userId the ID of the user to check
     * @return true if the user exists, false otherwise
     */
    public boolean checkUserExistence(Long userId) {
        return userRepository.existsById(userId);
    }

    public User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
