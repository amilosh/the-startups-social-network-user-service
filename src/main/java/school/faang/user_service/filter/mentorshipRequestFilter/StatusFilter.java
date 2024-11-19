package school.faang.user_service.filter.mentorshipRequestFilter;


import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class StatusFilter implements Filter<MentorshipRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getStatus().equals(filter.getStatus()));
    }
}
