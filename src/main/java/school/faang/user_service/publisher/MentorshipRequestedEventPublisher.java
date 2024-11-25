package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipRequestedEventDto;

@Slf4j
@Component
@RequiredArgsConstructor

public class MentorshipRequestedEventPublisher implements MessagePublisher<MentorshipRequestedEventDto> {

    private final RedisTemplate<String, MentorshipRequestedEventDto> redisTemplate;

    @Qualifier("mentorshipRequestedEventChannel")
    private final ChannelTopic mentorshipRequestedEventChannel;

    @Override
    @Retryable(retryFor = RuntimeException.class,
            backoff = @Backoff(delayExpression = "${spring.data.redis.publisher.delay}"))
    public void publish(MentorshipRequestedEventDto event) {
        try {
            redisTemplate.convertAndSend(mentorshipRequestedEventChannel.getTopic(), event);
            log.debug("Published mentorship requested event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish mentorship requested event: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}