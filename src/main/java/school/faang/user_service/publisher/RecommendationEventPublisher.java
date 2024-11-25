package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.RecommendationEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher implements EventPublisher<RecommendationEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RecommendationEvent event) {
        String channelName = redisProperties.getChannels().get("recommendation-event-channel");
        redisTemplate.convertAndSend(channelName, event);
        log.info("Published event {}", event);
    }
}
