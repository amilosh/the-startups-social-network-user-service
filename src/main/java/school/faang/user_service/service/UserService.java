package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public boolean checkUserExistence(long userId) {
        return userRepository.existsById(userId);
    }

    public User findUser(long userId) {
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
