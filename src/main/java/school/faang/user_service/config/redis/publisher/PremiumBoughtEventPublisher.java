package school.faang.user_service.config.redis.publisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;

@Service
public class PremiumBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public PremiumBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(PremiumBoughtEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
