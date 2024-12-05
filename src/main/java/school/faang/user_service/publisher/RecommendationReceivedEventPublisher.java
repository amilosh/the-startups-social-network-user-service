package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;
    private final ObjectMapper objectMapper;

    public void publish(RecommendationReceivedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(recommendationTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
