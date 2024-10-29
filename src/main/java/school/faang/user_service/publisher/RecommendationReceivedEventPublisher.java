package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements MessagePublisher<RecommendationReceivedEvent> {

    private final RedisTemplate<String, RecommendationReceivedEvent> redisTemplate;

    @Qualifier("recommendationReceivedTopic")
    private final ChannelTopic topic;

    @Override
    public void publish(RecommendationReceivedEvent recommendationReceivedEvent) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), recommendationReceivedEvent);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Not able to publish message to topic %s", topic.getTopic()), e
            );
        }
    }
}