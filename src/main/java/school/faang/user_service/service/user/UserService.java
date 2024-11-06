package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Get a user by its ID.
     *
     * @param userId the ID of the user
     * @return the user, or null if the user does not exist
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
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

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return the user with the specified ID
     * @throws EntityNotFoundException if no user with the given ID is found
     */
    public User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

    /**
     * Deletes a user from the database.
     *
     * @param user the user to delete
     */
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * Saves the provided user to the database.
     *
     * @param user the user to save
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
