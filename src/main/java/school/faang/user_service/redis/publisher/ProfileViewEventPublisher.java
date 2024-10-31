package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.redis.event.ProfileViewEvent;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher {
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties properties;
    private final ObjectMapper mapper;

    public void publish(ProfileViewEvent event) {
        if (Objects.isNull(event) || Objects.isNull(event.watchingId()) || Objects.isNull(event.watchedId())) {
            throw new IllegalStateException("Can't publish profile view event: neither event nor it's content can be null");
        }
        try {
            String data = mapper.writeValueAsString(event);
            redisTemplate.convertAndSend(properties.getProfileViewChannelName(), data);
        } catch(JsonProcessingException e) {
            log.error("Faced issues during serialization of ProfileViewEvent");
            throw new RuntimeException("Faced ProfileViewEvent serialization issue");
        }
    }
}
