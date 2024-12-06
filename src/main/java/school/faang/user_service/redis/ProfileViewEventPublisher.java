package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.ProfileViewEvent;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher{
    @Value("${spring.data.redis.channels.profile-view-channel.name}")
    private String profileView;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ProfileViewEvent event) {
        redisTemplate.convertAndSend(profileView, event);
    }
}
