package school.faang.user_service.filter.recommendationRequest;

import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestCreatedAfterFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return filterDto.getCreatedAfter() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto filterDto) {
        recommendationRequests.filter(request -> request.getCreatedAt().isAfter(filterDto.getCreatedAfter()));
    }
}
