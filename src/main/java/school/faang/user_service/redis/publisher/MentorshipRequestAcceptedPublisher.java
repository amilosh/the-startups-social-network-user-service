package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestAcceptedDto;
import school.faang.user_service.entity.MentorshipRequest;

@Aspect
@Component
public class MentorshipRequestAcceptedPublisher extends AbstractEventPublisher<MentorshipRequestAcceptedDto> {
    @Value("${spring.data.redis.channel-topics.mentorship.request_accepted}")
    private String mentorshipRequestAcceptedTopicName;

    public MentorshipRequestAcceptedPublisher(RedisTemplate<String, Object> redisTemplate,
                                              ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @AfterReturning(
            pointcut = "@annotation(school.faang.user_service.annotation.event.SendMentorshipRequestAcceptedEvent)",
            returning = "returnValue"
    )
    public void publishEvent(Object returnValue) {
        MentorshipRequest mentorshipRequest = (MentorshipRequest) returnValue;
        MentorshipRequestAcceptedDto eventDto = MentorshipRequestAcceptedDto.builder()
                .requestId(mentorshipRequest.getId())
                .receiverName(mentorshipRequest.getReceiver().getUsername())
                .actorId(mentorshipRequest.getRequester().getId())
                .build();
        publish(eventDto);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(mentorshipRequestAcceptedTopicName);
    }
}
