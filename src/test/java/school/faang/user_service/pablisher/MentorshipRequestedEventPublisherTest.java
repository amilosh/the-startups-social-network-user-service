package school.faang.user_service.pablisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.MentorshipRequestEvent;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MentorshipRequestedEventPublisher publisher;

    private String topicName;
    MentorshipRequestEvent event;
    String jsonEvent;

    @BeforeEach
    void setUp() {
        topicName = "mentorship-requested-topic";
        ReflectionTestUtils.setField(publisher, "mentorshipRequestedTopicName", topicName);
        event = new MentorshipRequestEvent(1L, 2L, null);
        jsonEvent = "{\"receiverId\":1, \"actorId\":2, \"receivedAt\":null}";

    }

    @Test
    void testPublishEventToRedisSuccessfully() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(jsonEvent);

        CompletableFuture<Void> result = publisher.publish(event);


        verify(redisTemplate, times(1)).convertAndSend(topicName, jsonEvent);
        verify(objectMapper, times(1)).writeValueAsString(event);
        assert (result.isDone());
    }

    @Test
    void testPublishHandleJsonProcessingException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        CompletableFuture<Void> result = publisher.publish(event);

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
        assert (result.isCompletedExceptionally());
    }

    @Test
    void testPublish_ShouldHandleGeneralException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenReturn(jsonEvent);


        doThrow(new RuntimeException("Unexpected error"))
                .when(redisTemplate).convertAndSend(topicName, jsonEvent);
        CompletableFuture<Void> result = publisher.publish(event);


        verify(redisTemplate, times(1)).convertAndSend(topicName, jsonEvent);
        assertTrue(result.isCompletedExceptionally());
    }
}
