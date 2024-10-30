package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.MentorshipRequestFilter;

import java.util.stream.Stream;

@Component
public class StatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto requestFilterDto) {
        mentorshipRequestStream.filter(mentorshipRequest -> mentorshipRequest.getStatus().name().equals(requestFilterDto.getStatus()));
    }
}
