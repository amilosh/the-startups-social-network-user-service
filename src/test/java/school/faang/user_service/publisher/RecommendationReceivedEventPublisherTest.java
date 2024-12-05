package school.faang.user_service.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.RecommendationReceivedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationReceivedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Test
    @DisplayName("Publish message in redis success")
    void testPublish_success() {
        RecommendationReceivedEvent event = new RecommendationReceivedEvent(1L, 2L, 3L);
        String channelName = "recommendation_channel";
        RedisProperties.Channel channel = new RedisProperties.Channel();
        channel.setRecommendationChannel(channelName);

        when(redisProperties.getChannel()).thenReturn(channel);

        recommendationReceivedEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(channelName, event);
    }
}