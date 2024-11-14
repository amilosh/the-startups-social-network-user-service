package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
@Component
public class CreatedAfterFilter implements RequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedAfter() != null;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        return recommendationRequest.getCreatedAt().isAfter(requestFilterDto.getCreatedAfter());
    }
}
