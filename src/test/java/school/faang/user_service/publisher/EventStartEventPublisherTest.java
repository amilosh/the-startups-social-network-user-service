package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.event.EventStartEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import school.faang.user_service.exception.EventPublishingException;
import school.faang.user_service.model.event.EventStartEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventStartEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic topic;

    @InjectMocks
    private EventStartEventPublisher eventStartEventPublisher;

    private EventStartEvent testEvent;

    @BeforeEach
    public void setUp() {
        testEvent = new EventStartEvent();
    }

    @Test
    @DisplayName("Should publish event successfully")
    public void testPublishEvent_Success() throws JsonProcessingException {
        String serializedEvent = "serialized event";
        when(objectMapper.writeValueAsString(testEvent)).thenReturn(serializedEvent);
        when(topic.getTopic()).thenReturn("eventStartTopic");

        eventStartEventPublisher.publish(testEvent);

        verify(redisTemplate, times(1)).convertAndSend(eq("eventStartTopic"), eq(serializedEvent));
    }

    @Test
    @DisplayName("Should throw EventPublishingException when serialization fails")
    public void testPublishEvent_SerializationFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(testEvent)).thenThrow(new JsonProcessingException("Test") {});

        assertThrows(EventPublishingException.class, () -> eventStartEventPublisher.publish(testEvent));

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw EventPublishingException for unexpected error")
    public void testPublishEvent_UnexpectedError() {
        when(topic.getTopic()).thenReturn("eventStartTopic");
        doThrow(new RuntimeException("Test")).when(redisTemplate).convertAndSend(anyString(), anyString());

        assertThrows(EventPublishingException.class, () -> eventStartEventPublisher.publish(testEvent));
    }
}