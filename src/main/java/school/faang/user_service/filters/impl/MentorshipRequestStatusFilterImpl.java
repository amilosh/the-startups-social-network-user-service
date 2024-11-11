package school.faang.user_service.filters.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filters.abstracts.MentorshipRequestFilter;

import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilterImpl implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream,
                                           RequestFilterDto requestFilterDto) {
        return mentorshipRequestStream.filter(mentorshipRequest -> mentorshipRequest
                .getStatus().equals(requestFilterDto.getStatus()));
    }
}
