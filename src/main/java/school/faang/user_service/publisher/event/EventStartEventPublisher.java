package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStartEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.event-start-event-channel.name}")
    private String eventStartEventTopic;

    public void publish(EventStartEvent eventStartEvent) {
        try {
            String json = objectMapper.writeValueAsString(eventStartEvent);
            redisTemplate.convertAndSend(eventStartEventTopic, json);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while working with JSON: ", e);
            throw new RuntimeException(e);
        }
    }
}

