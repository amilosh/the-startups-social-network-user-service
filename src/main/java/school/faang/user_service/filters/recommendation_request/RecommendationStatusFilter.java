package school.faang.user_service.filters.recommendation_request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;

@Component
@Slf4j
public class RecommendationStatusFilter implements RecommendationRequestFilter {
    @Override
    public boolean isFilterApplicable(RequestFilterDto requestFilterDto) {
        boolean applicable = requestFilterDto.getStatus() != null;
        log.info("Checking if filter 'RecommendationStatusFilter' is applicable. Status: {}, Result: {}",
                requestFilterDto.getStatus(), applicable);
        return applicable;
    }

    @Override
    public boolean apply(RecommendationRequest recommendationRequest, RequestFilterDto requestFilterDto) {
        RequestStatus statusOriginal = recommendationRequest.getStatus();
        RequestStatus statusFilter = requestFilterDto.getStatus();

        boolean result = Objects.equals(statusOriginal, statusFilter);
        log.info("Applying filter 'RecommendationStatusFilter'. Recommendation Status: {}, Filter Status: {}, Result: {}",
                statusOriginal, statusFilter, result);
        return result;
    }
}
