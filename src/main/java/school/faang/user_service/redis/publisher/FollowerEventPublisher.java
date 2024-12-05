package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements EventPublisher<FollowerEvent> {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.channel.follower-event-channel.name}")
    private String followerEventChannel;

    @Override
    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(followerEventChannel, event);
    }
}
