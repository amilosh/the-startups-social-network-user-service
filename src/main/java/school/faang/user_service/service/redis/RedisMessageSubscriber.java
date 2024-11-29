package school.faang.user_service.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageContent = message.toString();
        log.info("Message received: " + messageContent);
        userService.handleUserBanMessage(messageContent);
    }

}
