package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class StatusFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter) {
        RequestStatus status = filter.getStatus();
        return stream.filter(request -> request.getStatus() == status);
    }
}
