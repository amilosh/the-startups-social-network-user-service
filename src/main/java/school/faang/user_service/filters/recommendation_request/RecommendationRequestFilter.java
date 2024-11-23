package school.faang.user_service.filters.recommendation_request;

import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

public interface RecommendationRequestFilter {

    boolean isFilterApplicable(RequestFilterDto requestFilterDto);

    boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto);
}
