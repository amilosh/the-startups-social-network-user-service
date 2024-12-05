package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic recommendationTopic;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RecommendationReceivedEventPublisher publisher;

    @Test
    public void testSuccessfulPublish() throws JsonProcessingException {
        RecommendationReceivedEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("some_json");
        when(recommendationTopic.getTopic()).thenReturn("some_topic");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(recommendationTopic.getTopic(), "some_json");
    }

    @Test
    public void testPublishWithJsonProcessingException() throws JsonProcessingException {
        RecommendationReceivedEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        assertEquals(RuntimeException.class, exception.getClass());
    }

    private RecommendationReceivedEvent prepareEvent() {
        return new RecommendationReceivedEvent(1L, 2L, 3L, "Content", LocalDateTime.now());
    }
}
