package school.faang.user_service.publisher.goal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher {

    @Value("${spring.data.redis.channels.goal-completed-channel.name}")
    private String topicGoalCompleted;

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    public void publish(GoalCompletedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topicGoalCompleted, json);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while working with JSON: ", e);
            throw new RuntimeException(e);
        }
    }


}
