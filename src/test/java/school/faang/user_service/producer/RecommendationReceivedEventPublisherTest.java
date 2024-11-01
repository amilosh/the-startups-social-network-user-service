package school.faang.user_service.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.model.event.RecommendationReceivedEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationReceivedEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic recommendationReceivedTopic;

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @Test
    void publish_isOk() {
        // given
        var recommendationReceivedEvent = RecommendationReceivedEvent.builder().build();
        // when
        recommendationReceivedEventPublisher.publish(recommendationReceivedEvent);
        // then
        verify(redisTemplate).send(recommendationReceivedTopic.name(), recommendationReceivedEvent);
    }
}
