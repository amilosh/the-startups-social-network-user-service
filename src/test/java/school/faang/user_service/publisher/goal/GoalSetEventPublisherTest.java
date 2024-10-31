package school.faang.user_service.publisher.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.goal.GoalSetEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalSetEventPublisherTest {

  private final String topicName = "goalSetTopic";
  
  @InjectMocks
  private GoalSetEventPublisher goalSetEventPublisher;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ChannelTopic goalSetTopic;

  @Test
  @DisplayName("Should send goal set event message to Redis")
  void whenPublishThenSendMessageToRedis() {
    GoalSetEvent event = GoalSetEvent.builder().build();
    when(goalSetTopic.getTopic()).thenReturn(topicName);

    goalSetEventPublisher.publish(event);

    ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

    verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

    assertEquals(topicName, topicCaptor.getValue());
    assertSame(event, messageCaptor.getValue());
  }
}
