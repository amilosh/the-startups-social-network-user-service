package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.recommendation_topic}")
    private String channel;

    public void publish(RecommendationEvent event)  {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
