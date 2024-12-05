package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.recommendation-topic}")
    private String channel;

    public void publish(RecommendationEvent event) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(event);
        redisTemplate.convertAndSend(channel, json);
    }

}
