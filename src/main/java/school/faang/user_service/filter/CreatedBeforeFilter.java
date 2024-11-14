package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
@Component
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
