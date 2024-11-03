package school.faang.user_service.service.mentorship.request_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;
@Component
public class RequestReceiverFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getReceiverId() != null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filters) {
        mentorshipRequests.filter(request -> request.getReceiver().getId().equals(filters.getReceiverId()));
    }
}