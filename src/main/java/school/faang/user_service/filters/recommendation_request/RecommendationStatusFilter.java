package school.faang.user_service.filters.recommendation_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;

@Component
public class RecommendationStatusFilter implements RecommendationRequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        RequestStatus statusOriginal = recommendationRequest.getStatus();
        RequestStatus statusFilter = requestFilterDto.getStatus();
        return Objects.equals(statusOriginal, statusFilter);
    }
}
