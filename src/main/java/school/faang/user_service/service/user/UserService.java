package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final AvatarService avatarService;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    public String generateRandomAvatar() {
        Long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String randomAvatarUrl = avatarService.generateRandomAvatar(UUID.randomUUID().toString(),
                userId + ".svg");
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(randomAvatarUrl);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        return randomAvatarUrl;
    }
}
