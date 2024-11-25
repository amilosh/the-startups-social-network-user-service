package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("user not found"));

        user.setBanned(true);
        userRepository.save(user);
    }
}
