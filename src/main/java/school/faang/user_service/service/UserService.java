package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void updateAllUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                EntityNotFoundException("User do not found by " + userId));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
