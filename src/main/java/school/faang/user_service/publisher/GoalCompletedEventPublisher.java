package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.GoalCompletedEvent;

@Component
public class GoalCompletedEventPublisher extends AbstractEventPublisher<GoalCompletedEvent> {
    @Autowired
    public GoalCompletedEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic goalCompletedTopic) {
        super(redisTemplate, goalCompletedTopic);
    }
}
