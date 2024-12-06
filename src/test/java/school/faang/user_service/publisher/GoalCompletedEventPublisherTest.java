package school.faang.user_service.publisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalCompletedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GoalCompletedEventPublisher publisher;

    @Value("${spring.data.redis.channels.goal_topic}")
    private String channel;

    @Test
    public void testSuccessfulPublish() throws JsonProcessingException {
        GoalCompletedEvent event = setEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("some_json");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(channel, "some_json");
    }

    @Test
    public void testPublishWithJsonProcessingException() throws JsonProcessingException {
        GoalCompletedEvent event = setEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        Assertions.assertEquals(RuntimeException.class, exception.getClass());
    }

    public GoalCompletedEvent setEvent() {
        return new GoalCompletedEvent(1L, 1L, LocalDateTime.now());
    }
}
