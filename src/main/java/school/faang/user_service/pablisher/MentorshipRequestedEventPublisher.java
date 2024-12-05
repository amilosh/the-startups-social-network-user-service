package school.faang.user_service.pablisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestEvent;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${redis.topic.mentorship-requested}")
    private String mentorshipRequestedTopicName;

    @Async("taskExecutor")
    public CompletableFuture<Void> publish(MentorshipRequestEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(mentorshipRequestedTopicName, json);
            log.info("Successfully published mentorship request event to Redis topic.");
            return CompletableFuture.completedFuture(null);
        } catch (JsonProcessingException e) {
            log.error("Error processing event: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}