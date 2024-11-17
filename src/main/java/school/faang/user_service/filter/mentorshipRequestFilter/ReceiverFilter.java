package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter;

import java.util.stream.Stream;

@Component
public class ReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverUserId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, MentorshipRequestFilterDto requestFilterDto) {
        return mentorshipRequestStream.filter(mentorshipRequest ->
                mentorshipRequest.getReceiver().getId().equals(requestFilterDto.getReceiverUserId()));
    }
}
