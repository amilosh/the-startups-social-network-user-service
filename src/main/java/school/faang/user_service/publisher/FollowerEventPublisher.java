package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.FollowerEvent;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEvent> {

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Qualifier("followerEventChannelTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
    @Override
    public void publish(FollowerEvent event) {
        super.publish(event);
    }
}