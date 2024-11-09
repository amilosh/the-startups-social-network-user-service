package school.faang.user_service.filter.MentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class RequesterIdFilter implements Filter<MentorshipRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getRequester().getId().equals(filter.getRequesterId()));
    }
}
