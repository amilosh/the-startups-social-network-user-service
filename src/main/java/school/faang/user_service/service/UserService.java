package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}