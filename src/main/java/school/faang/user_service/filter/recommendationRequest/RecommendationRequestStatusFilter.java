package school.faang.user_service.filter.recommendationRequest;

import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestStatusFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto filterDto) {
        recommendationRequests.filter(request -> request.getStatus().equals(filterDto.getStatus()));
    }
}
