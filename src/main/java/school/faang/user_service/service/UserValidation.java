package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public void isUserExists(long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public void isUserExists(long firstUserId, long secondUserId) {
        isUserExists(firstUserId);
        isUserExists(secondUserId);
    }
}
