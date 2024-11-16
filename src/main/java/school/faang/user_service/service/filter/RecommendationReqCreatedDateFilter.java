package school.faang.user_service.service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.stream.Stream;

public class RecommendationReqCreatedDateFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getCreatedAt() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> requests, RequestFilterDto filter) {
        requests.filter(request -> request.getCreatedAt() == filter.getCreatedAt());
    }
}
