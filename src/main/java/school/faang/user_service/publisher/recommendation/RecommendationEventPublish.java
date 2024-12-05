package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublish implements MessagePublish<RecommendationEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel-topic}")
    private String channelTopic;

    @Override
    public void publish(RecommendationEvent event) {
        try {
            String eventString = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic, eventString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
