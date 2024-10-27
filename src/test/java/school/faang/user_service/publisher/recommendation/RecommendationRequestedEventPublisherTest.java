package school.faang.user_service.publisher.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.message.RecommendationRequestedEventMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestedEventPublisherTest {

    private static final String TOPIC_NAME = "Test";

    @InjectMocks
    private RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Test
    @DisplayName("Should send message")
    void whenGetMessageThenSendMessage() {
        RecommendationRequestedEventMessage event = RecommendationRequestedEventMessage.builder().build();
        when(channelTopic.getTopic()).thenReturn(TOPIC_NAME);

        recommendationRequestedEventPublisher.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertEquals(TOPIC_NAME, topicCaptor.getValue());
        assertSame(event, messageCaptor.getValue());
    }
}