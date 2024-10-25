package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.ProfileViewEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileViewEventPublisherTest {
    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RedisProperties redisProperties;
    private ProfileViewEvent event;
    private String channelName;
    private Map<String,String> channelsMap;

    @BeforeEach
    void setUp() {
        channelName = "channelName";
        event = new ProfileViewEvent();
        channelsMap = new HashMap<>();
        channelsMap.put("profile_view_event",channelName);
        redisProperties.setChannels(channelsMap);
    }

    @Test
    void publish() {
        Map<String, String> channelsMap = new HashMap<>();
        channelsMap.put("profile_view_event", channelName);

        when(redisProperties.getChannels()).thenReturn(channelsMap);

        profileViewEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(channelName, event);
    }
}
