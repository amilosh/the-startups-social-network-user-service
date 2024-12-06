package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscribeEventDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnfollowEventPublisher {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private final ObjectMapper objectMapper;

    public void publish(SubscribeEventDTO event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            redisTemplate.convertAndSend("unfollower_channel", json);

            log.info("Опубликовано событие: {} для FollowerId: {} и FolloweeId: {}",
                event.getEventType(), event.getFollowerId(), event.getFolloweeId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации события отписки", e);
            throw new RuntimeException("Ошибка сериализации события отписки", e);
        }
    }
}
