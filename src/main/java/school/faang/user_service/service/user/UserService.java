package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
