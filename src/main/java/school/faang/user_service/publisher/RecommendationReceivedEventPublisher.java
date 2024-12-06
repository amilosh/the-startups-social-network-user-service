package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.RecommendationReceivedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements MessagePublisher<RecommendationReceivedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(RecommendationReceivedEvent event) {
        redisTemplate.convertAndSend(redisProperties.getChannel().getRecommendationChannel(), event);
        log.info("Message sent to channel: {}", redisProperties.getChannel().getRecommendationChannel());
    }
}
