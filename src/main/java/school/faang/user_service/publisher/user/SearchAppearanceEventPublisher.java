package school.faang.user_service.publisher.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.SearchAppearanceEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchAppearanceEventPublisher {

    @Value("${spring.data.redis.channels.search-appearance-channel.name}")
    private String topicSearchAppearanceEvent;

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    public void publish(SearchAppearanceEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topicSearchAppearanceEvent, json);
        } catch (JsonProcessingException e) {
            log.error("An error occurred while working with JSON: ", e);
            throw new RuntimeException(e);
        }
    }
}