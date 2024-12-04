package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;

@Component
@RequiredArgsConstructor
public class PremiumBoughtPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${redis.topic.premium-bought}")
    private String premiumBoughtTopic;

    public void publish(PremiumBoughtEvent event) {
        redisTemplate.convertAndSend(premiumBoughtTopic, event);
    }
}
