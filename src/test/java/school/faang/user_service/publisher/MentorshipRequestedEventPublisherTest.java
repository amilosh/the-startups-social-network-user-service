package school.faang.user_service.publisher;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.MentorshipRequestedEventDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestedEventPublisherTest {

    @Mock
    private RedisTemplate<String, MentorshipRequestedEventDto> mentorshipRequestedEventRedisTemplate;

    @Mock
    private ChannelTopic mentorshipRequestedEventChannel;

    @InjectMocks
    private MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Test
    public void testPublish() {
        MentorshipRequestedEventDto event = MentorshipRequestedEventDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .requestedAt(LocalDateTime.now())
                .build();

        when(mentorshipRequestedEventChannel.getTopic()).thenReturn("mentorship_requested_channel");

        mentorshipRequestedEventPublisher.publish(event);

        verify(mentorshipRequestedEventRedisTemplate, times(1))
                .convertAndSend("mentorship_requested_channel", event);
    }

    @Test
    public void testPublishWithException() {
        MentorshipRequestedEventDto event = MentorshipRequestedEventDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .requestedAt(LocalDateTime.now())
                .build();

        when(mentorshipRequestedEventChannel.getTopic()).thenReturn("mentorship_requested_channel");
        doThrow(new RuntimeException("Redis error"))
                .when(mentorshipRequestedEventRedisTemplate)
                .convertAndSend("mentorship_requested_channel", event);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mentorshipRequestedEventPublisher.publish(event));
        assertEquals("Redis error", exception.getCause().getMessage());
    }

}