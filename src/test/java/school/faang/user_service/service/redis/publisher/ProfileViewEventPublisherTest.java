package school.faang.user_service.service.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.redis.event.ProfileViewEvent;
import school.faang.user_service.redis.publisher.ProfileViewEventPublisher;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewEventPublisherTest {
    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private RedisProperties properties;

    @Mock
    private StringRedisTemplate redisTemplate;

    ProfileViewEvent testEvent = new ProfileViewEvent(1L, 2L);
    String testEventAsString = "{\"requestingId\":1,\"requestedId\":2}";
    String channelName = "profile_view";

    @Test
    public void testPublish() throws JsonProcessingException {
        when(mapper.writeValueAsString(testEvent)).thenReturn(testEventAsString);
        when(properties.getProfileViewChannelName()).thenReturn(channelName);
        when(redisTemplate.convertAndSend(properties.getProfileViewChannelName(), testEventAsString))
                .thenReturn(1L);

        profileViewEventPublisher.publish(testEvent);
        verify(redisTemplate, times(1))
                .convertAndSend(properties.getProfileViewChannelName(), testEventAsString);
    }

    @Test
    public void testPublish_NullEvent() {
        ProfileViewEvent testEvent = new ProfileViewEvent(null, null);
        assertThrows(IllegalArgumentException.class,
                () -> profileViewEventPublisher.publish(null));
        assertThrows(IllegalArgumentException.class,
                () -> profileViewEventPublisher.publish(testEvent));
    }

    @Test
    public void testPublish_FailingJsonProcessing() throws JsonProcessingException {
        when(mapper.writeValueAsString(testEvent)).thenThrow(JsonProcessingException.class);
        assertThrows(RuntimeException.class,
                () -> profileViewEventPublisher.publish(testEvent));
    }
}
