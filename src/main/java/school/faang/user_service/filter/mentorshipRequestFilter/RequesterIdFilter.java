package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class RequesterIdFilter implements Filter<MentorshipRequest, MentorshipRequestFilterDto> {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filter) {
        return requests.filter(request -> request.getRequester().getId().equals(filter.getRequesterId()));
    }
}