package school.faang.user_service.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.service.user.UserService;

@AllArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private UserService userService;

    public void onMessage(Message message, byte[] pattern) {
        userService.banUser(Long.parseLong(new String(message.getBody())));
    }
}