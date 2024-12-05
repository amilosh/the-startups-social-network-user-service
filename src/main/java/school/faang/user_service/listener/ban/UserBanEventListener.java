package school.faang.user_service.listener.ban;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.ban.UserBanEvent;
import school.faang.user_service.listener.AbstractEventListener;
import school.faang.user_service.service.user.UserService;

@Service
public class UserBanEventListener extends AbstractEventListener<UserBanEvent> {
    private final UserService userService;

    public UserBanEventListener(ObjectMapper objectMapper, UserService userService) {
        super(objectMapper);
        this.userService = userService;
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        handleEvent(message, UserBanEvent.class, event -> userService.banUser(event.userId()));
    }
}
