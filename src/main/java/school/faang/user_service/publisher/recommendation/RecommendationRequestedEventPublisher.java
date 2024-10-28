package school.faang.user_service.publisher.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.message.RecommendationRequestedEventMessage;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestedEventPublisher implements MessagePublisher<RecommendationRequestedEventMessage> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationRequestEventTopic;

    @Override
    public void publish(RecommendationRequestedEventMessage message) {
        log.info("message publish: {}", message.toString());
        redisTemplate.convertAndSend(recommendationRequestEventTopic.getTopic(), message);
    }
}
