package school.faang.user_service.filters.recommendation_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
@Component
public class CreatedBeforeFilterRecommendation implements RecommendationRequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedBefore() != null;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        return recommendationRequest.getCreatedAt().isBefore(requestFilterDto.getCreatedBefore());
    }
}
