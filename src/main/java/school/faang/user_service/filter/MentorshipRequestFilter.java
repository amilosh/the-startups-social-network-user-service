package school.faang.user_service.filter;

import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto requestFilterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, MentorshipRequestFilterDto requestFilterDto);
}
