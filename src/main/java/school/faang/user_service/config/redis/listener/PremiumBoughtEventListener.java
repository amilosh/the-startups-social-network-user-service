package school.faang.user_service.config.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;
import school.faang.user_service.service.event.PremiumEventService;

@Slf4j
@Component
public class PremiumBoughtEventListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final PremiumEventService premiumEventService;

    public PremiumBoughtEventListener(ObjectMapper objectMapper, PremiumEventService premiumEventService) {
        this.objectMapper = objectMapper;
        this.premiumEventService = premiumEventService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String eventJson = new String(message.getBody());
        log.info("Received a message from Redis: {}", eventJson);

        try {
            PremiumBoughtEvent event = objectMapper.readValue(eventJson, PremiumBoughtEvent.class);
            premiumEventService.processEvent(event);
            log.info("Processed PremiumBoughtEvent: {}", event);
        } catch (Exception e) {
            log.error("Failed to deserialize PremiumBoughtEvent", e);
        }
    }
}

