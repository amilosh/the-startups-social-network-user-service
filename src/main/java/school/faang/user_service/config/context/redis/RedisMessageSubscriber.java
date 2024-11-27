package school.faang.user_service.config.context.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            List<Long> idForBan = objectMapper.readValue(message.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
            userService.banUsers(idForBan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
