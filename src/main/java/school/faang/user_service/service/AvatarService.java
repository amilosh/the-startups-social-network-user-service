package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.AvatarServiceValidator;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarServiceValidator avatarServiceValidator;
    public String generateAvatar(User user) {
        avatarServiceValidator.checkUser(user);
        String url = "https://api.dicebear.com/9.x/avataaars/svg?seed=" + user.getUsername();
        return url;
    }
}


