package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.RecommendationReceivedEvent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Spy
    private RedisProperties redisProperties;

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Captor
    private ArgumentCaptor<RecommendationReceivedEvent> RecommendationReceivedEventCaptor;

    private final String channelName = "recommendation-received-channel";

    @BeforeEach
    void setUp() {
        Map<String, String> channels = Map.of("recommendation-received-channel", channelName);
        redisProperties.setChannels(channels);
    }

    @Test
    void publish_ShouldSendEventToRedisChannel() {
        RecommendationReceivedEvent event = new RecommendationReceivedEvent();

        recommendationReceivedEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(eq(channelName), RecommendationReceivedEventCaptor.capture());
        RecommendationReceivedEvent capturedEvent = RecommendationReceivedEventCaptor.getValue();
        assertEquals(event, capturedEvent);
    }
}
