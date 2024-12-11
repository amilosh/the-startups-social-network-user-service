package school.faang.user_service.redis.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestAcceptedDto;
import school.faang.user_service.entity.mentorship.MentorshipRequest;
import school.faang.user_service.entity.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestAcceptedPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MentorshipRequestAcceptedPublisher mentorshipRequestPublisher;

    private MentorshipRequest mentorshipRequest;
    private MentorshipRequestAcceptedDto eventDto;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(mentorshipRequestPublisher,
                "mentorshipRequestAcceptedTopicName", "mentorship_requests");
        mentorshipRequest = new MentorshipRequest();
        User user1 = User.builder()
                .id(2L)
                .build();
        User user2 = User.builder()
                .id(3L)
                .build();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setReceiver(user1);
        mentorshipRequest.setRequester(user2);

        eventDto = MentorshipRequestAcceptedDto.builder()
                .requestId(mentorshipRequest.getId())
                .actorId(mentorshipRequest.getRequester().getId())
                .receiverName(mentorshipRequest.getReceiver().getUsername())
                .build();
    }

    @Test
    public void testPublishEvent1() throws Exception {
        when(objectMapper.writeValueAsString(any(MentorshipRequestAcceptedDto.class))).thenReturn(eventDto.toString());

        mentorshipRequestPublisher.publishEvent(mentorshipRequest);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertEquals("mentorship_requests", topicCaptor.getValue());
        assertEquals(eventDto.toString(), messageCaptor.getValue());
    }

    @Test
    public void testPublishEventJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any(MentorshipRequestAcceptedDto.class)))
                .thenThrow(new JsonProcessingException("Error") {});

        mentorshipRequestPublisher.publishEvent(mentorshipRequest);

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}