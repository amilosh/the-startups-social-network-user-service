package school.faang.user_service.service.user;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Long> getNotExistingUserIds(List<Long> userIds) {
        return userIds.isEmpty() ? Collections.emptyList() : userRepository.findNotExistingUserIds(userIds);
    }
}
