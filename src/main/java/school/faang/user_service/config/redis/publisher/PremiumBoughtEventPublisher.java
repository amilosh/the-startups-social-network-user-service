package school.faang.user_service.config.redis.publisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import school.faang.user_service.config.redis.events.PremiumBoughtEvent;
@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumBoughtEventPublisher {
@Autowired
    private final RedisTemplate<String, Object> redisTemplate;
@Autowired
    private final ChannelTopic topic;

//    public PremiumBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
//        this.redisTemplate = redisTemplate;
//        this.topic = topic;
//    }

    public void publish(PremiumBoughtEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }

}
