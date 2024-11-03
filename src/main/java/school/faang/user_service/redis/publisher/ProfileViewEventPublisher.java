package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.redis.event.ProfileViewEvent;
import java.util.Objects;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher {
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties properties;
    private final ObjectMapper mapper;

    public void publish(ProfileViewEvent event) {
        if (Objects.isNull(event) || Objects.isNull(event.requestingId()) || Objects.isNull(event.requestedId())) {
            throw new IllegalStateException("Can't publish profile view event: neither event nor it's content can be null");
        }
        try {
            String data = mapper.writeValueAsString(event);
            redisTemplate.convertAndSend(properties.getProfileViewChannelName(), data);
            log.info("Published profile view event: requesting - {}, requested - {}", event.requestingId(), event.requestedId() );
        } catch(JsonProcessingException e) {
            log.error("Faced issues during serialization of ProfileViewEvent");
            throw new RuntimeException("Faced ProfileViewEvent serialization issue");
        }
    }
}
