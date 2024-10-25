package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.event.FollowerEvent;

import static org.mockito.Mockito.*;

class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic followerEventChannelTopic;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publish_shouldSendFollowerEventToRedis() throws JsonProcessingException {
        FollowerEvent event = new FollowerEvent(1L, 2L, null, null);
        String serializedEvent = "{\"subscriberUserId\":1,\"subscribedToUserId\":2,\"subscribedToProjectId\":null}";

        when(objectMapper.writeValueAsString(event)).thenReturn(serializedEvent);

        followerEventPublisher.publish(event);

        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(redisTemplate, times(1)).convertAndSend(followerEventChannelTopic.getTopic(), serializedEvent);
    }
}