package school.faang.user_service.publisher.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.entity.goal.Goal;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {

    private RedisTemplate<String, Object> redisTemplate;

    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend("goal_completed_topic", event);
    }





}
