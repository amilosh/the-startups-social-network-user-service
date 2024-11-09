package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestedEventDto;
import school.faang.user_service.entity.MentorshipRequest;

@Aspect
@Component
public class MentorshipRequestPublisher extends AbstractEventPublisher<MentorshipRequestedEventDto> {
    @Value("${spring.data.redis.channel-topics.mentorship.request_received}")
    private String mentorshipRequestReceivedTopicName;

    public MentorshipRequestPublisher(RedisTemplate<String, Object> redisTemplate,
                                      ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @AfterReturning(
            pointcut = "@annotation(school.faang.user_service.annotation.event.SendMentorshipRequestReceived)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue) {
        MentorshipRequest mentorshipRequest = (MentorshipRequest) returnValue;
        MentorshipRequestedEventDto eventDto = new MentorshipRequestedEventDto(
                mentorshipRequest.getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getCreatedAt());
        publish(eventDto);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(mentorshipRequestReceivedTopicName);
    }
}
