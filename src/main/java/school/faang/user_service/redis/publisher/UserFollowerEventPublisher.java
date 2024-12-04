package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.UserFollowerEvent;

@Component
@RequiredArgsConstructor
public class UserFollowerEventPublisher {

    @Value("${spring.data.redis.channel.follower}")
    private String userFollowerChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(UserFollowerEvent event) {
        redisTemplate.convertAndSend(userFollowerChannel, event);
    }
}
