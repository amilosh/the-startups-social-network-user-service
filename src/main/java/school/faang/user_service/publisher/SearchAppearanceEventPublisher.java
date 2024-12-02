package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.SearchAppearanceEvent;

@Service
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic searchAppearanceTopic;

    public void publishSearchAppearanceEvent(SearchAppearanceEvent searchAppearanceEvent) {
        redisTemplate.convertAndSend(searchAppearanceTopic.getTopic(), searchAppearanceEvent);
    }
}