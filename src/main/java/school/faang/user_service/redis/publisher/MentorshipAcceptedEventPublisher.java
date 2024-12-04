package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.MentorshipAcceptedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher {

    @Value("${spring.data.redis.channel.mentorship.accepted}")
    private String mentorshipAcceptedChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(MentorshipAcceptedEvent event) {
        redisTemplate.convertAndSend(mentorshipAcceptedChannel, event);
    }
}
