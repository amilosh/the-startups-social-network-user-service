package school.faang.user_service.service.mentorship.request_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;
@Component
public interface RequestFilter {

    boolean isApplicable(RequestFilterDto filters);

    void apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filters);
}
