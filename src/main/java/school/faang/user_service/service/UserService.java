package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " not found"));
    }
    public void deleteUser (User user){
        userRepository.delete(user);
    }
}
