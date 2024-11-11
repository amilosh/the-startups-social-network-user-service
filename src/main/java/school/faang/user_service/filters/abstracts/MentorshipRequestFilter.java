package school.faang.user_service.filters.abstracts;

import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto requestFilterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream,
                                           RequestFilterDto requestFilterDto);
}
