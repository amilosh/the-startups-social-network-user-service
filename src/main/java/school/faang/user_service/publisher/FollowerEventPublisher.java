package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.FollowerEvent;

@Service
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent> {
    private final RedisTemplate<String, FollowerEvent> redisTemplate;
    private final ChannelTopic followingTopic;

    @Autowired
    public FollowerEventPublisher(@Qualifier("followerEventPublisherRedisTemplate")
                                      RedisTemplate<String, FollowerEvent> redisTemplate,
                                  @Qualifier("follower-channel") ChannelTopic followingTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.followingTopic = followingTopic;
    }

    @Override
    public void publish(FollowerEvent message) {
        redisTemplate.convertAndSend(followingTopic.getTopic(), message);
    }
}
