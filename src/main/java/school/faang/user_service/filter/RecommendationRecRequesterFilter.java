package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRecRequesterFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> requests, RequestFilterDto filter) {
        requests.filter(request -> request.getRequester().getId().equals(filter.getRequesterId()));
    }
}
