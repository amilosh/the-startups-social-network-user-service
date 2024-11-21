package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements EventPublisher<RecommendationReceivedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RecommendationReceivedEvent event) {
        String channelName = redisProperties.getChannels().get("recommendation-received-channel");
        redisTemplate.convertAndSend(channelName, event);
    }
}
