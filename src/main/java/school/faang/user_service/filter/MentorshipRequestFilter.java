package school.faang.user_service.filter;

import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto requestFilterDto);

    void apply(Stream<MentorshipRequest> mentorshipRequestStream, MentorshipRequestFilterDto requestFilterDto);
}
