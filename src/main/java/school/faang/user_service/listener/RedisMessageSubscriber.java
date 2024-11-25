package school.faang.user_service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.service.UserService;

public class RedisMessageSubscriber implements MessageListener {
    @Autowired
    private UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
//        Long userId = Long.valueOf(new String(message.getBody()));
//        userService.banUser(userId);
        System.out.println(message.toString());
    }
}
