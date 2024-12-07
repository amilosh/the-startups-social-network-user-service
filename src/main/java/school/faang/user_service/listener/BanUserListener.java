package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.BanUserEvent;
import school.faang.user_service.service.UserService;

@RequiredArgsConstructor
@Component
@Slf4j
public class BanUserListener implements MessageListener {
    private final UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String convertedMessage = new String(message.getBody());
        BanUserEvent banUserEvent;
        try {
            log.warn("mapping message to banUserEvent class");
            banUserEvent = objectMapper.readValue(convertedMessage, BanUserEvent.class);
        } catch (JsonProcessingException e) {
            log.error("Throw IllegalArgumentException because can't read value ", e);
            throw new IllegalArgumentException("Неверный Json класс", e);
        }

        userService.banUser(banUserEvent);
    }
}
