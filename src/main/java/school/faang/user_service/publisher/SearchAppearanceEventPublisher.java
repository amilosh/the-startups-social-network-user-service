package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.controller.event.SearchAppearanceEvent;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchAppearanceEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic searchAppearanceTopic;

    public void publishSearchAppearanceEvent(SearchAppearanceEvent searchAppearanceEvent) {
        log.info("Publishing SearchAppearanceEvent to Redis: {}", searchAppearanceEvent);
        redisTemplate.convertAndSend(searchAppearanceTopic.getTopic(), searchAppearanceEvent);
    }
}

