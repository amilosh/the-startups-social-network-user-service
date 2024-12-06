package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements EventPublisher<FollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.follower-event-channel}")
    private String followerEventChannel;

    @Override
    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(followerEventChannel, event);
        log.info("Follower event published: {}", event);
    }
}
