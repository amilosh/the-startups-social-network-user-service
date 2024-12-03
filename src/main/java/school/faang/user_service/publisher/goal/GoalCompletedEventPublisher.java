package school.faang.user_service.publisher.goal;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.entity.goal.Goal;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher {

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    public void publish(GoalCompletedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend("goal_completed_topic", json);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while working with JSON: ", e);
            throw new RuntimeException(e);
        }
    }


}
