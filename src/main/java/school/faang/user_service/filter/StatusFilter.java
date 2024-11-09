package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;

@Component
public class StatusFilter implements RequestFilter {
    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        RequestStatus statusOriginal = recommendationRequest.getStatus();
        RequestStatus statusFilter = requestFilterDto.getStatus();
        return Objects.equals(statusOriginal, statusFilter);
    }
}
