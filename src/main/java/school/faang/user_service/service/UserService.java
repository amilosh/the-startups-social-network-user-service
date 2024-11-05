package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id: %s not found".formatted(id)));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
