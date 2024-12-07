package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final UserRepository userRepository;

    @Transactional
    public void banUsers(List<Long> userIds) {
        List<User> bannedUsers = new ArrayList<>();
        userRepository.findAllByIds(userIds)
                .ifPresentOrElse(
                        users -> users.stream()
                                .peek(user -> {
                                    user.setBanned(true);
                                    bannedUsers.add(user);
                                })
                                .toList(),
                        () -> {
                            log.info("users by ids: {}, not found!", userIds);
                            return;
                        }
                );
        log.info("users success banned, users: {}", bannedUsers);
    }
}
