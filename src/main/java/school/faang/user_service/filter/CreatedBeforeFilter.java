package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

public class CreatedBeforeFilter implements RequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedBefore() != null;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        return recommendationRequest.getCreatedAt().isBefore(requestFilterDto.getCreatedBefore());
    }
}
