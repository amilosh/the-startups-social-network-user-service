package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final ObjectMapper objectMapper;

    public void publish(FollowerEvent followerEvent) {
        try {
            String json = objectMapper.writeValueAsString(followerEvent);
            redisTemplate.convertAndSend("follower_channel", followerEvent);
            log.info("Опубликованное событие подписчика для FollowerId: {} и FolloweeId: {}", followerEvent.getFollowerId(), followerEvent.getFolloweeId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации события подписчика", e);
            throw new RuntimeException("Ошибка сериализации события подписчика", e);
        }
    }
}
