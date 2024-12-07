package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.events.GoalCompletedEvent;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalCompletedEventPublisherTest {
    @InjectMocks
    private GoalCompletedEventPublisher goalCompletedEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic goalCompletedTopic;
    private String topic = "topic";

    @Test
    void testSuccessfulSendingMessage() {
        GoalCompletedEvent goalCompletedEvent = new GoalCompletedEvent();
        goalCompletedEvent.setGoalId(1);
        goalCompletedEvent.setUserId(3);

        Mockito.when(goalCompletedTopic.getTopic()).thenReturn(topic);

        goalCompletedEventPublisher.publish(goalCompletedEvent);

        Mockito.verify(redisTemplate).convertAndSend(topic, goalCompletedEvent);
    }
}