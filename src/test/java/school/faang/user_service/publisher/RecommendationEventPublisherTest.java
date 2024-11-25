package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.RecommendationEvent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Spy
    private RedisProperties redisProperties;

    @InjectMocks
    private RecommendationEventPublisher recommendationEventPublisher;

    @Captor
    private ArgumentCaptor<RecommendationEvent> recommendationEventCaptor;

    private final String channelName = "recommendation-event-channel";

    @BeforeEach
    void setUp() {
        Map<String, String> channels = Map.of("recommendation-event-channel", channelName);
        redisProperties.setChannels(channels);
    }

    @Test
    void publish_ShouldSendEventToRedisChannel() {
        RecommendationEvent event = new RecommendationEvent();

        recommendationEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(eq(channelName), recommendationEventCaptor.capture());
        RecommendationEvent capturedEvent = recommendationEventCaptor.getValue();
        assertEquals(event, capturedEvent);

    }
}
