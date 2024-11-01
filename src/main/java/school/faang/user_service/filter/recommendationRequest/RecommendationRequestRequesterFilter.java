package school.faang.user_service.filter.recommendationRequest;

import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

public class RecommendationRequestRequesterFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return filterDto.getRequesterId() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto filterDto) {
        recommendationRequests.filter(request -> Objects.equals(request.getRequester().getId(), filterDto.getRequesterId()));
    }
}
