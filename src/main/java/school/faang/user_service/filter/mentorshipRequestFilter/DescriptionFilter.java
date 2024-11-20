package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements Filter<MentorshipRequest, MentorshipRequestFilterDto> {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return filter.getDescriptionPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filter) {
        return requests.filter(request -> request.getDescription().toLowerCase()
                .contains(filter.getDescriptionPattern().toLowerCase()));
    }
}
