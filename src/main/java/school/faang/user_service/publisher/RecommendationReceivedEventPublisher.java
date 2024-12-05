package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements MessagePublisher<RecommendationReceivedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RecommendationReceivedEvent event) {
        redisTemplate.convertAndSend(redisProperties.getChannel().getRecommendationChannel(), event);
    }
}
