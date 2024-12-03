package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.UsersBanEvent;
import school.faang.user_service.exceptions.MessageMappingException;
import school.faang.user_service.service.UserService;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsersBanListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("Received an event to ban users");
            UsersBanEvent usersBanEvent = objectMapper.readValue(message.getBody(), UsersBanEvent.class);
            userService.banUsers(usersBanEvent.userIdsToBan());
        } catch (IOException e) {
            throw new MessageMappingException("Failed to map message to UsersBanEvent");
        }
    }
}
